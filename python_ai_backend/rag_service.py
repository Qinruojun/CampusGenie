import json
import os
import re
import time
import traceback
from dataclasses import dataclass, field
from threading import RLock
from typing import Callable, Optional

import pymysql
import requests
from langchain_community.chat_models import ChatOpenAI
from langchain_community.vectorstores import FAISS
from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
from langchain_core.prompts import ChatPromptTemplate
from sentence_transformers import SentenceTransformer

from rag_config import DEBUG_MODE, RagConfig, load_rag_config


# 本文件是 RAG 问答的核心服务层。
# 主要流程：
# 1. 从 MySQL 读取已发布问答，转换成 LangChain Document
# 2. 用嵌入模型构建或加载 FAISS 向量库
# 3. 调用意图识别服务，优先在对应分类中检索
# 4. 命中本地知识库后交给大模型生成回答
# 5. 本地知识库未命中时，调用博查 AI Search 兜底


@dataclass(frozen=True)
class RagAnswer:
    """统一 RAG 输出格式，API 层会把 kb_id 映射成 knowledge_id。"""
    answer: str
    kb_id: Optional[int] = None
    related_questions: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        return {
            "answer": self.answer,
            "kb_id": self.kb_id,
            "related_questions": self.related_questions,
        }


@dataclass(frozen=True)
class ConversationTurn:
    """一轮历史对话，用于后续问题的指代消解。"""

    question: str
    answer: str


class ConversationMemory:
    """简单的内存型会话历史。

    当前实现保存在 Python 进程内，适合单机服务。
    如果后续多进程部署或服务重启后也要保留历史，可以替换成 Redis / 数据库实现。
    """

    def __init__(self, max_rounds: int):
        self.max_rounds = max_rounds
        self._sessions: dict[str, list[ConversationTurn]] = {}
        self._lock = RLock()

    def get_history(self, session_id: str) -> list[ConversationTurn]:
        with self._lock:
            return list(self._sessions.get(session_id, []))

    def add_turn(self, session_id: str, question: str, answer: str) -> None:
        with self._lock:
            turns = self._sessions.setdefault(session_id, [])
            turns.append(ConversationTurn(question=question, answer=answer))
            self._sessions[session_id] = turns[-self.max_rounds :]

    def clear(self, session_id: str) -> None:
        with self._lock:
            self._sessions.pop(session_id, None)


class ModelScopeEmbeddings(Embeddings):
    """LangChain Embeddings 适配器，把 SentenceTransformer 包装成向量化接口。"""

    def __init__(self, model_name: str = "BAAI/bge-small-zh-v1.5"):
        if DEBUG_MODE:
            print(f"[DEBUG] 正在加载嵌入模型: {model_name}")
        self.model = SentenceTransformer(model_name)
        if DEBUG_MODE:
            print("[DEBUG] 嵌入模型加载完成")

    def embed_documents(self, texts: list[str]) -> list[list[float]]:
        start_time = time.time()
        embeddings = self.model.encode(texts, normalize_embeddings=True).tolist()
        if DEBUG_MODE:
            print(f"[DEBUG] 完成 {len(texts)} 个文档的向量化，耗时: {time.time() - start_time:.2f}s")
        return embeddings

    def embed_query(self, text: str) -> list[float]:
        start_time = time.time()
        embedding = self.model.encode(text, normalize_embeddings=True).tolist()
        if DEBUG_MODE:
            print(f"[DEBUG] 完成查询向量化，耗时: {time.time() - start_time:.2f}s")
        return embedding


class KnowledgeRepository:
    """知识库读取层，只负责从数据库取出可用于 RAG 的知识文档。"""

    def __init__(self, config: RagConfig):
        self.config = config

    def load_published_documents(self) -> list[Document]:
        """读取 status=1 的知识条目，并转换成 Document 列表。"""
        print("正在连接数据库并构建知识块...")
        connection = None
        documents: list[Document] = []

        try:
            connection = pymysql.connect(
                host=self.config.database.host,
                user=self.config.database.user,
                password=self.config.database.password,
                database=self.config.database.database,
                charset=self.config.database.charset,
            )
            with connection.cursor(pymysql.cursors.DictCursor) as cursor:
                # 只取已发布知识。category_id 会作为 metadata，后面用于分类过滤检索。
                cursor.execute(
                    "SELECT id, question, answer, category_id "
                    "FROM knowledge_base WHERE status = 1"
                )
                for row in cursor.fetchall():
                    documents.append(
                        Document(
                            page_content=f"问题：{row['question']}\n答案：{row['answer']}",
                            metadata={
                                "kb_id": row["id"],
                                "category_id": row["category_id"],
                                "question": row["question"],
                            },
                        )
                    )
        except Exception as exc:
            raise RuntimeError(f"数据库连接或读取失败：{exc}") from exc
        finally:
            if connection is not None and connection.open:
                connection.close()

        if not documents:
            raise RuntimeError("错误：数据库中没有找到已发布的知识条目！")

        print(f"成功从数据库读取并转化为 {len(documents)} 个问答对（知识块）")
        return documents


class VectorStoreFactory:
    """向量库工厂，负责创建或复用本地 FAISS 索引。"""

    def __init__(self, config: RagConfig):
        self.config = config

    def build(self, documents: list[Document], embeddings: Embeddings) -> FAISS:
        index_file = os.path.join(self.config.vectorstore_path, "index.faiss")
        print(f"向量数据库路径: {self.config.vectorstore_path}")

        if os.path.exists(index_file):
            print("加载已存在的向量数据库...")
            vectorstore = FAISS.load_local(
                self.config.vectorstore_path,
                embeddings,
                allow_dangerous_deserialization=True,
            )
        else:
            print("正在创建向量数据库...")
            os.makedirs(self.config.vectorstore_path, exist_ok=True)
            vectorstore = FAISS.from_documents(documents, embeddings)
            vectorstore.save_local(self.config.vectorstore_path)
            print("向量数据库已成功保存到本地")

        if vectorstore.index.ntotal == 0:
            raise RuntimeError("错误：向量数据库为空！")

        print(f"向量数据库中共有 {vectorstore.index.ntotal} 个文档")
        return vectorstore


class IntentClassifierClient:
    """意图识别客户端，根据问题预测知识分类 ID。"""

    def __init__(self, config: RagConfig):
        self.config = config

    def classify(self, question: str) -> Optional[int]:
        headers = {
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": f"Bearer {self.config.intent.token}",
        }
        payload = json.dumps({"question": question})

        try:
            response = requests.post(
                self.config.intent.url,
                data=payload,
                headers=headers,
                timeout=self.config.intent.timeout,
            )
            response.raise_for_status()
            return response.json().get("category_id")
        except Exception as exc:
            if DEBUG_MODE:
                print(f"[WARNING] 意图识别服务未运行，已跳过。错误: {exc}")
            return None


class BochaAISearchClient:
    """博查 AI Search 客户端，本地知识库无法回答时使用。"""

    def __init__(self, config: RagConfig):
        self.config = config

    def search(self, question: str) -> str:
        api_key = os.getenv("BOCHA_API_KEY")
        if not api_key:
            return "错误：未找到 BOCHA_API_KEY！"

        headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json",
        }
        payload = {
            "query": f"关于华南理工大学：{question}",
            "stream": False,
            "model": self.config.bocha_model,
        }

        try:
            start_time = time.time()
            response = requests.post(
                self.config.bocha_api_url,
                headers=headers,
                json=payload,
                timeout=self.config.bocha_timeout,
            )

            if DEBUG_MODE:
                print("\n" + "-" * 30)
                print(f"[DEBUG] 博查 API HTTP状态码: {response.status_code}")
                print(f"[DEBUG] 博查 API 原始响应文本（最多显示 2000 字符）: {response.text[:2000]}")
                print("-" * 30 + "\n")

            response.raise_for_status()
            data = response.json()
            self._debug_payload(data)

            if data.get("code") != 200:
                return f"博查API业务报错: {data.get('msg')} (错误码: {data.get('code')})"

            for message in data.get("messages", []):
                if message.get("type") == "answer":
                    if DEBUG_MODE:
                        print(f"[DEBUG] 博查 AI 搜索耗时: {time.time() - start_time:.2f}s")
                    return self._clean_answer(message.get("content", ""))

            if DEBUG_MODE:
                print("[DEBUG] 未找到 type=answer 的 message，请查看上方博查返回内容。")
            return "博查接口请求成功，但在返回的 messages 中未找到 answer 类型的回答。"
        except Exception as exc:
            return f"博查 AI 搜索调用失败: {exc}"

    def _debug_payload(self, data: dict) -> None:
        if not DEBUG_MODE:
            return

        print("\n" + "-" * 30)
        print("[DEBUG] 博查返回字段:", list(data.keys()))
        messages = data.get("messages", [])
        if messages:
            print(f"[DEBUG] 博查 messages 数量: {len(messages)}")
            for idx, message in enumerate(messages, start=1):
                content = str(message.get("content", "")).strip()
                print(f"[DEBUG] message[{idx}] type={message.get('type', 'unknown')}")
                if content:
                    print(content[:2000])
        else:
            print("[DEBUG] 博查返回中没有 messages 字段或 messages 为空。")

        raw_text = json.dumps(data, ensure_ascii=False, indent=2)
        print("[DEBUG] 博查原始 JSON（最多显示 4000 字符）:")
        print(raw_text[:4000])
        print("-" * 30 + "\n")

    @staticmethod
    def _clean_answer(answer: str) -> str:
        """去掉博查返回文本中的 [引用:数字] 标记，避免直接展示给前端。"""
        return re.sub(r"\[引用:\d+\]", "", answer).strip()


class RagQAService:
    """RAG 编排服务，是业务层最核心的类。"""

    PROMPT_TEMPLATE = """
    你是校园百事通的智能助手，只能基于以下提供的知识库上下文和对话历史回答问题。
    如果用户的问题里有“它、这个、那里、上述”等代词，要优先结合对话历史判断指代对象。

    对话历史：
    {chat_history}

    知识库上下文：
    {context}

    当前问题：{question}

    回答：
    """

    def __init__(
        self,
        config: RagConfig,
        vectorstore: FAISS,
        llm: ChatOpenAI,
        prompt: ChatPromptTemplate,
        intent_classifier: IntentClassifierClient,
        web_search: BochaAISearchClient,
        memory: ConversationMemory,
    ):
        self.config = config
        self.vectorstore = vectorstore
        self.llm = llm
        self.prompt = prompt
        self.intent_classifier = intent_classifier
        self.web_search = web_search
        self.memory = memory

    @classmethod
    def create(cls, config: Optional[RagConfig] = None) -> "RagQAService":
        """系统启动时调用：加载数据、向量库、提示词、大模型和外部客户端。"""
        config = config or load_rag_config()
        documents = KnowledgeRepository(config).load_published_documents()

        print("正在加载嵌入模型...")
        embeddings = ModelScopeEmbeddings(model_name=config.embedding_model)
        vectorstore = VectorStoreFactory(config).build(documents, embeddings)

        prompt = ChatPromptTemplate.from_template(cls.PROMPT_TEMPLATE)
        print("提示词模板创建完成")

        print("正在初始化大模型...")
        print(f"大模型接口地址: {config.llm_api_base}")
        print(f"大模型名称: {config.model_name}")
        llm = ChatOpenAI(
            model_name=config.model_name,
            temperature=config.temperature,
            max_tokens=config.max_tokens,
            openai_api_key=config.llm_api_key,
            openai_api_base=config.llm_api_base,
        )
        print("大模型初始化完成")

        print("RAG系统初始化完成！")
        return cls(
            config=config,
            vectorstore=vectorstore,
            llm=llm,
            prompt=prompt,
            intent_classifier=IntentClassifierClient(config),
            web_search=BochaAISearchClient(config),
            memory=ConversationMemory(config.memory_rounds),
        )

    def ask(self, question: str, session_id: str = "default") -> dict:
        """回答单个问题：先查本地知识库，失败后走全网搜索兜底。"""
        total_start_time = time.time()
        history = self.memory.get_history(session_id)
        chat_history = self._format_history(history)
        retrieval_question = self._build_retrieval_question(question, history)
        docs_with_scores = self._retrieve(retrieval_question)
        related_questions = self._search_related_questions(retrieval_question, question)
        print(f"[DEBUG] 数据库检索耗时: {time.time() - total_start_time:.2f}s")

        if docs_with_scores:
            if DEBUG_MODE:
                print(f"[DEBUG] 本地库检索成功，最佳相似度得分: {docs_with_scores[0][1]:.4f}")

            context = self._format_docs(docs_with_scores)
            messages = self.prompt.format_messages(
                chat_history=chat_history,
                context=context,
                question=question,
            )
            response = self.llm.invoke(messages)

            if "抱歉，我没有找到相关信息" not in response.content:
                if DEBUG_MODE:
                    print(f"[DEBUG] 本地检索总耗时: {time.time() - total_start_time:.2f}s")
                matched_kb_id = docs_with_scores[0][0].metadata.get("kb_id")
                rag_answer = RagAnswer(
                    answer=response.content.strip(),
                    kb_id=matched_kb_id,
                    related_questions=related_questions,
                )
                self.memory.add_turn(session_id, question, rag_answer.answer)
                return rag_answer.to_dict()

        print("\n[INFO] 本地知识库未命中，触发博查 AI 搜索引擎直连...")
        web_question = self._build_web_search_question(question, chat_history)
        rag_answer = RagAnswer(
            answer=self.web_search.search(web_question).strip(),
            related_questions=related_questions,
        )
        self.memory.add_turn(session_id, question, rag_answer.answer)
        return rag_answer.to_dict()

    def _retrieve(self, question: str) -> list[tuple[Document, float]]:
        """两级检索：先按意图分类精确检索，未命中再全局检索。"""
        print("\n[INFO] 正在分析问题意图...")
        target_category_id = self.intent_classifier.classify(question)
        docs_with_scores: list[tuple[Document, float]] = []

        if target_category_id is not None:
            if DEBUG_MODE:
                print(f"[INFO] 锁定分类 ID: {target_category_id}，正在执行精确检索...")
            docs_with_scores = self._search(question, filter={"category_id": target_category_id})

        if docs_with_scores:
            return docs_with_scores

        if DEBUG_MODE:
            reason = "未识别出明确分类" if target_category_id is None else "对应分类库中无高匹配度答案"
            print(f"[INFO] {reason}，触发全局向量检索...")
        return self._search(question)

    def _search(self, question: str, filter: Optional[dict] = None) -> list[tuple[Document, float]]:
        """执行 FAISS 相似度检索，并按距离阈值过滤低相关结果。"""
        raw_docs = self.vectorstore.similarity_search_with_score(
            question,
            k=self.config.retrieval_k,
            filter=filter,
        )
        return [
            (doc, score)
            for doc, score in raw_docs
            if score < self.config.distance_threshold
        ]

    def _search_related_questions(self, retrieval_question: str, current_question: str) -> list[str]:
        """为前端推荐相似问题；不使用回答阈值，尽量保证每次都有 3 个推荐。"""
        try:
            raw_docs = self.vectorstore.similarity_search_with_score(
                retrieval_question,
                k=max(self.config.retrieval_k * 4, 12),
            )
            return self._build_related_questions(raw_docs, current_question)
        except Exception as exc:
            if DEBUG_MODE:
                print(f"[WARNING] 相关问题推荐生成失败: {exc}")
            return []

    @staticmethod
    def _format_docs(docs_with_scores: list[tuple[Document, float]]) -> str:
        """把检索结果拼成大模型提示词中的上下文文本。"""
        return "\n\n".join(doc.page_content for doc, _score in docs_with_scores)

    @classmethod
    def _build_related_questions(
        cls,
        docs_with_scores: list[tuple[Document, float]],
        current_question: str,
        limit: int = 3,
    ) -> list[str]:
        """直接从相似度最高的检索结果中提取推荐问题。"""
        related_questions: list[str] = []
        normalized_current = current_question.strip()

        for doc, _score in docs_with_scores:
            question = cls._extract_question(doc).strip()
            if not question or question == normalized_current:
                continue
            if question in related_questions:
                continue

            related_questions.append(question)
            if len(related_questions) >= limit:
                break

        return related_questions

    @staticmethod
    def _extract_question(doc: Document) -> str:
        """优先从 metadata 取原问题；旧索引没有该字段时，从 page_content 兜底解析。"""
        question = doc.metadata.get("question")
        if question:
            return str(question)

        match = re.search(r"问题：(.+?)(?:\n答案：|$)", doc.page_content, re.S)
        if match:
            return match.group(1).strip()
        return ""

    @staticmethod
    def _format_history(history: list[ConversationTurn]) -> str:
        """把最近几轮对话拼成提示词里的历史上下文。"""
        if not history:
            return "无"

        lines: list[str] = []
        for index, turn in enumerate(history, start=1):
            lines.append(f"第{index}轮用户：{turn.question}")
            lines.append(f"第{index}轮助手：{turn.answer}")
        return "\n".join(lines)

    @staticmethod
    def _build_retrieval_question(question: str, history: list[ConversationTurn]) -> str:
        """把历史问题拼到检索查询中，提升代词问题的召回率。"""
        if not history:
            return question

        recent_questions = "\n".join(turn.question for turn in history[-3:])
        return f"历史问题：\n{recent_questions}\n当前问题：{question}"

    @staticmethod
    def _build_web_search_question(question: str, chat_history: str) -> str:
        """全网搜索兜底时也带上历史，避免搜索一个孤立代词问题。"""
        if chat_history == "无":
            return question
        return f"对话历史：\n{chat_history}\n当前问题：{question}"

    def clear_memory(self, session_id: str = "default") -> None:
        """清空某个会话的历史，适合用户点击“新对话”时调用。"""
        self.memory.clear(session_id)


def initialize_rag_system() -> Callable[..., dict]:
    """兼容旧调用方式：返回一个可直接 rag_chain(question) 的函数。"""
    return RagQAService.create().ask


def answer_question(
    question: str,
    rag_chain: Callable[..., dict],
    session_id: str = "default",
) -> dict | str:
    """统一问答入口，捕获异常并返回用户可读错误。"""
    try:
        return rag_chain(question, session_id=session_id)
    except Exception as exc:
        print("\n" + "!" * 80)
        print("[ERROR] 回答过程中发生异常:")
        print("!" * 80)
        traceback.print_exc()
        print("!" * 80 + "\n")
        return f"回答出错：{exc}"
