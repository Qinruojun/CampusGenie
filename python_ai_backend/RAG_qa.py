"""
当用户输入一个问题后，系统会按照以下“三级降级策略”来寻找并生成答案：
1. 第一级：意图识别与精准检索
  - 系统首先会调用一个外部 API（为训练的意图识别小模型，本地部署，需要通过 ngrok 内网穿透），试图识别用户问题所属的类别（Category）。
  - 如果识别出类别，系统会在 FAISS 向量数据库中通过 metadata 过滤，专门针对该类别的知识块进行精确检索。
2. 第二级：全局向量检索（整个知识兜底）
  - 如果意图识别失败，或者精确检索没有找到相关度高的答案（距离分数大于设定的阈值 1.1），系统会降级为在整个向量数据库中进行全局相似度检索。
  - 如果找到了靠谱的上下文，系统会将内容提交给大模型，要求其基于本地知识生成回答
3. 第三级：联网搜索兜底（全网知识兜底）
  - 如果本地知识库完全没有命中，或者大模型判定本地知识不足以回答，系统会再次降级，触发搜索引擎。
  - 它会先用大模型重写用户的搜索关键词（强制加上“华南理工大学”等限定词），然后调用 DuckDuckGo 搜索全网最新信息。
  - 最后，大模型基于网页搜索结果生成回答（并打上 【联网搜索结果】 标签）。

系统初始化与数据处理模块
在进入问答循环之前，initialize_rag_system 函数做了大量的准备工作：
- 直连 MySQL 提取知识： 直接连接远程 MySQL 数据库 (CampusGenie)，拉取状态为“已发布”的 QA（问答）对，并拼接成 LangChain 所需的 Document 格式，同时保留分类 ID 等元数据。
- 服务器本地向量库管理： * 将所有 QA 对通过特定的嵌入模型转化为向量。
  - 如果本地已经存在 FAISS 索引文件，则直接加载（节省启动时间）；如果没有，则重新创建并保存到本地磁盘。
"""
import os
import json
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"
os.environ["TRANSFORMERS_OFFLINE"] = "0"
import sys
import time
import torch
import numpy as np
from dotenv import load_dotenv
from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import FAISS
from langchain_core.embeddings import Embeddings
from langchain_core.prompts import ChatPromptTemplate
from langchain_community.chat_models import ChatOpenAI
from typing import List
from sentence_transformers import SentenceTransformer
import pymysql
from langchain_core.documents import Document
import requests

# 调试开关：设置为False可一键关闭所有调试信息
DEBUG_MODE = True

# 环境变量配置
os.environ["TOKENIZERS_PARALLELISM"] = "false"
os.environ["HF_HUB_DISABLE_SYMLINKS_WARNING"] = "1"

# 加载环境变量
load_dotenv()

# 获取当前脚本所在的目录
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

# 配置参数
CONFIG = {
    "text_file_path": os.path.join(SCRIPT_DIR, "1_classification_log.json"),
    "chunk_size": 300,
    "chunk_overlap": 40,
    "embedding_model": "BAAI/bge-small-zh-v1.5",
    "model_name": "deepseek-v4-pro",
    "temperature": 0.1,
    "max_tokens": 800,
    "vectorstore_path": os.path.join(os.path.expanduser("~"), ".scut_rag_vector", "faiss_index")
}

class ModelScopeEmbeddings(Embeddings):
    def __init__(self, model_name="BAAI/bge-small-zh-v1.5"):
        if DEBUG_MODE:
            print(f"[DEBUG] 正在加载嵌入模型: {model_name}")
        self.model = SentenceTransformer(model_name)
        if DEBUG_MODE:
            print(f"[DEBUG] 嵌入模型加载完成")

    def embed_documents(self, texts):
        start_time = time.time()
        embeddings = self.model.encode(texts, normalize_embeddings=True).tolist()
        if DEBUG_MODE:
            print(f"[DEBUG] 完成 {len(texts)} 个文档的向量化，耗时: {time.time()-start_time:.2f}s")
        return embeddings

    def embed_query(self, text):
        start_time = time.time()
        embedding = self.model.encode(text, normalize_embeddings=True).tolist()
        if DEBUG_MODE:
            print(f"[DEBUG] 完成查询向量化，耗时: {time.time()-start_time:.2f}s")
        return embedding

def bocha_ai_search(question: str) -> str:
    """
    直连博查 AI-Search 接口，直接获取大模型生成的答案
    """
    api_key = os.getenv("BOCHA_API_KEY")
    if not api_key:
        return "错误：未找到 BOCHA_API_KEY！"

    # 注意这里的接口地址变成了 ai-search
    url = "https://api.bochaai.com/v1/ai-search"
    
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    payload = {
        # 为了提高准确率，我们可以把用户的提问稍微包装一下，加上“华南理工大学”的限定
        "query": f"关于华南理工大学：{question}",
        "stream": False,  # 如果你的前端不支持流式输出，设置为 False 直接拿完整回答
        "model": "bocha-glm-4" # 博查支持的可选模型参数，具体可查阅其最新文档
    }
    
    try:
        start_time = time.time()
        response = requests.post(url, headers=headers, json=payload, timeout=15)
        
        # if DEBUG_MODE:
        #     print("\n" + "-"*30)
        #     print(f"[DEBUG] 博查 API HTTP状态码: {response.status_code}")
        #     print(f"[DEBUG] 博查 API 原始返回 JSON: {response.text}")
        #     print("-"  *30 + "\n")

        response.raise_for_status()
        data = response.json()
        
        # 检查博查的业务状态码（通常 200 代表成功）
        if data.get("code") != 200:
            return f"博查API业务报错: {data.get('msg')} (错误码: {data.get('code')})"
            
        # 🔥 正确解析博查的 messages 列表结构
        ai_answer = ""
        messages = data.get("messages", [])
        for msg in messages:
            # 遍历列表，找到类型为 'answer' 的那一条
            if msg.get("type") == "answer":
                ai_answer = msg.get("content", "")
                break
        
        if not ai_answer:
            return "博查接口请求成功，但在返回的 messages 中未找到 answer 类型的回答。"
            
        if DEBUG_MODE:
            print(f"[DEBUG] 博查 AI 搜索耗时: {time.time()-start_time:.2f}s")
            
        return ai_answer
        
    except Exception as e:
        return f"博查 AI 搜索调用失败: {str(e)}"

def initialize_rag_system():
    # 1 & 2. 直连数据库读取并按QA对切分
    print("正在连接数据库并构建知识块...")
    chunks = []
    
    # 数据库配置
    db_config = {
        "host": "8.148.158.43",
        "user": "dev",
        "password": "Campus@Dev#DB123456",
        "database": "CampusGenie",
        "charset": "utf8mb4"
    }
    
    try:
        # 连接数据库
        connection = pymysql.connect(**db_config)
        with connection.cursor(pymysql.cursors.DictCursor) as cursor:
            # 仅读取已发布状态 (status=1) 的知识
            sql = "SELECT id, question, answer, category_id FROM knowledge_base WHERE status = 1"
            cursor.execute(sql)
            records = cursor.fetchall()
            
            for row in records:
                # 拼接 page_content
                content = f"问题：{row['question']}\n答案：{row['answer']}"
                
                # 保留元数据
                metadata = {
                    "kb_id": row['id'],
                    "category_id": row['category_id']
                }
                
                doc = Document(page_content=content, metadata=metadata)
                chunks.append(doc)
                
    except Exception as e:
         raise RuntimeError(f"数据库连接或读取失败：{e}")
    finally:
        if 'connection' in locals() and connection.open:
            connection.close()

    if not chunks:
        raise RuntimeError("错误：数据库中没有找到已发布的知识条目！")
        
    print(f"成功从数据库读取并转化为 {len(chunks)} 个问答对（知识块）")
    
    # 3. 加载嵌入模型
    print("正在加载嵌入模型...")
    embeddings = ModelScopeEmbeddings(model_name=CONFIG["embedding_model"])
    
    # 4. 创建向量数据库
    index_file = os.path.join(CONFIG["vectorstore_path"], "index.faiss")
    print(f"向量数据库路径: {CONFIG['vectorstore_path']}")
    
    if os.path.exists(index_file):
        print("加载已存在的向量数据库...")
        vectorstore = FAISS.load_local(
            CONFIG["vectorstore_path"], 
            embeddings, 
            allow_dangerous_deserialization=True
        )
    else:
        print("正在创建向量数据库...")
        os.makedirs(CONFIG["vectorstore_path"], exist_ok=True)
        vectorstore = FAISS.from_documents(chunks, embeddings)
        vectorstore.save_local(CONFIG["vectorstore_path"])
        print("向量数据库已成功保存到本地")
    
    # 验证向量库
    if vectorstore.index.ntotal == 0:
        raise RuntimeError("错误：向量数据库为空！")
    print(f"向量数据库中共有 {vectorstore.index.ntotal} 个文档")
     
    # 5. 定义提示词模板
    prompt_template = """
    你是校园百事通的智能助手，只能基于以下提供的上下文回答问题。
    如果上下文中没有相关信息，请明确回答"抱歉，我在数据库中没有找到相关信息"。
    不要编造任何不在上下文中的内容，不要回答与问题无关的内容。
    回答要全面、准确、简洁，使用中文口语化表达。

    上下文：
    {context}

    问题：{question}

    回答：
    """
    
    prompt = ChatPromptTemplate.from_template(prompt_template)
    print("提示词模板创建完成")
    
    # 6. 初始化大模型
    print("正在初始化大模型...")
    # 从 .env 文件获取 Key，如果没拿到就报错提醒
    deepseek_api_key = os.getenv("DEEPSEEK_API_KEY")
    if not deepseek_api_key:
        raise ValueError("未找到 DEEPSEEK_API_KEY，请检查 .env 文件是否配置正确！")
        
    llm = ChatOpenAI(
        model_name=CONFIG["model_name"],
        temperature=CONFIG["temperature"],
        max_tokens=CONFIG["max_tokens"],
        openai_api_key=deepseek_api_key, # 这里直接使用刚才拿到的变量
        openai_api_base="https://api.deepseek.com/v1",
    )
    print("大模型初始化完成")
    
    # 8. 定义请求本地意图识别小模型的 API 函数
    def get_category_from_api(question):
        API_URL = "http://127.0.0.1:8088/classify" # 在服务器运行时需要改为http://127.0.0.1:15000/classify
        headers = {
            "Content-Type": "application/json; charset=utf-8",
            "Authorization": "Bearer 122333444455555"
        }
        payload_string = json.dumps({"question": question})
        try:
            start_time = time.time()
            response = requests.post(API_URL, data=payload_string, headers=headers, timeout=0.5)
            data = response.json()
            return data.get("category_id")
        except Exception as e:
            # 明确打印警告，但返回 None 触发系统降级逻辑
            if DEBUG_MODE:
                print(f"[WARNING] 意图识别服务未运行，已跳过。错误: {e}")
            return None 

    # 9. 构建新的多级 RAG 链
    def format_docs(docs):
        # 这里传入的是 tuple 列表 [(doc, score), ...]
        return "\n\n".join(doc.page_content for doc, score in docs)
    
    def rag_chain(question):
        total_start_time = time.time()
        
        # [配置项] 距离阈值设定 (FAISS 默认 L2 距离，越小越相似)
        # 对于 bge-small-zh，一般 1.0 ~ 1.2 是分水岭。如果发现找不准，可以把这个值调小(要求更严格)。
        DISTANCE_THRESHOLD = 1.1 
        
        docs_with_scores = []
        
        # 1. 调用远程 API 获取分类并精准检索 
        print("\n[INFO] 正在分析问题意图...")
        target_category_id = get_category_from_api(question)
        
        if target_category_id is not None:
            if DEBUG_MODE:
                print(f"[INFO] 锁定分类 ID: {target_category_id}，正在执行精确检索...")
            # 利用 metadata filter 进行精准搜索
            raw_docs = vectorstore.similarity_search_with_score(
                question, 
                k=3, 
                filter={"category_id": target_category_id}
            )
            # 过滤掉分数过高的（距离太远，不相关）
            docs_with_scores = [(doc, score) for doc, score in raw_docs if score < DISTANCE_THRESHOLD]
            
        # 2. 分类库未命中，降级为全局检索 
        if not docs_with_scores:
            if DEBUG_MODE:
                reason = "未识别出明确分类" if target_category_id is None else "对应分类库中无高匹配度答案"
                print(f"[INFO] {reason}，触发全局向量检索...")
            
            raw_docs = vectorstore.similarity_search_with_score(question, k=3)
            docs_with_scores = [(doc, score) for doc, score in raw_docs if score < DISTANCE_THRESHOLD]

        print(f"[DEBUG] 数据库检索耗时: {time.time() - total_start_time:.2f}s")     

        # 3. 本地库有靠谱结果，交由大模型生成
        if docs_with_scores:
            if DEBUG_MODE:
                print(f"[DEBUG] 本地库检索成功，最佳相似度得分: {docs_with_scores[0][1]:.4f}")
            
            context = format_docs(docs_with_scores)
            messages = prompt.format_messages(context=context, question=question)
            response = llm.invoke(messages)
            
            # 检查大模型是否依然认为上下文不足以回答
            if "抱歉，我在数据库中没有找到相关信息" not in response.content:
                if DEBUG_MODE:
                    print(f"[DEBUG] 本地检索总耗时: {time.time() - total_start_time:.2f}s")
                # 提取最匹配的第一条知识块的 kb_id
                matched_kb_id = docs_with_scores[0][0].metadata.get("kb_id")
                return {
                    "answer": f"📚【本地知识库】\n{response.content}",
                    "kb_id": matched_kb_id  # 把 ID 传出去
                }

        # 4. 本地全面溃败，启动博查 AI 搜索兜底 
        print("\n[INFO] 本地知识库未命中，触发博查 AI 搜索引擎直连...")
        
        # 直接把用户的原始问题扔给博查，无需提取关键词
        ai_search_answer = bocha_ai_search(question)
        
        return {
                "answer": f"【全网 AI 搜索总结】\n{ai_search_answer}",
                "kb_id": None
        }
    
    print("RAG系统初始化完成！")
    return rag_chain
    

def answer_question(question, rag_chain):
    """回答用户问题，包含完整调试信息"""
    try:
        return rag_chain(question)
    except Exception as e:
        import traceback
        print("\n" + "!"*80)
        print(f"[ERROR] 回答过程中发生异常:")
        print("!"*80)
        traceback.print_exc()
        print("!"*80 + "\n")
        return f"回答出错：{str(e)}"

# 初始化系统
rag_chain = initialize_rag_system()

if __name__ == "__main__":
    print("\n" + "="*50)
    print("校园百事通")
    print("输入你的问题，输入 'exit' 或 'quit' 退出系统")
    print(f"调试模式: {'开启' if DEBUG_MODE else '关闭'}")
    print("="*50 + "\n")
    
    try:
        while True:
            # 获取用户输入
            user_input = input("请输入问题: ").strip()
            
            # 检查退出指令
            if user_input.lower() in ["exit", "quit", "退出"]:
                print("\n感谢使用，再见！")
                break
            
            # 跳过空输入
            if not user_input:
                continue
            
            # 回答问题并打印
            print("\n正在思考...")
            answer = answer_question(user_input, rag_chain)
            print(f"\n回答: {answer}\n")
            print("-"*50 + "\n")
    
    except KeyboardInterrupt:
        print("\n\n检测到中断信号，系统正在退出...")
        print("感谢使用，再见！")
        sys.exit(0)
