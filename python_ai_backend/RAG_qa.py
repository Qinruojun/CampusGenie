"""
（此处本地指服务器）
本系统采用 RAG (检索增强生成) 技术，结合了“本地知识库优先 + 联网搜索兜底”的双层机制。
1. 本地知识检索：
       - 读取本地数据库数据 并进行文本分块
       - 使用 BAAI/bge-small-zh-v1.5 模型进行向量化，由 FAISS 构建并存储本地向量库
2. 大模型 (LLM) 生成：
       - 接入 DeepSeek 大语言模型 (deepseek-v4-pro)
       - 将检索到的本地文档作为上下文，利用预设 Prompt 生成准确、口语化的回答
3. 智能联网兜底 (Fallback)：
       - 若本地数据库未命中，自动触发大模型重写用户提问，提取包含“华南理工大学”的精准搜索关键词
       - 调用 DuckDuckGo 搜索引擎获取全网最新信息，并让大模型基于网络搜索结果重新生成最终答案
"""
import os
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
from langchain_community.tools import DuckDuckGoSearchResults
from langchain_community.utilities import DuckDuckGoSearchAPIWrapper
import pymysql
from langchain_core.documents import Document

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
    "vectorstore_path": os.path.join(os.path.expanduser("~"), ".scut_rag_total_V2", "faiss_index")
}

class ModelScopeEmbeddings(Embeddings):
    def __init__(self, model_name="BAAI/bge-small-zh-v1.5"):
        if DEBUG_MODE:
            print(f"[DEBUG] 正在加载嵌入模型: {model_name}")
        self.model = SentenceTransformer(model_name)
        if DEBUG_MODE:
            print(f"[DEBUG] 嵌入模型加载完成 ✅")

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
    
    # 5. 创建检索器
    retriever = vectorstore.as_retriever(search_kwargs={"k": 3})
    print("检索器创建完成")
    
    # 6. 定义提示词模板
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
    
    # 7. 初始化大模型
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
        openai_api_base="https://api.deepseek.com/v1"
    )
    print("大模型初始化完成")
    
    # 8. 初始化搜索引擎
    print("正在初始化搜索引擎...")
    # 配置搜索参数，设定区域和最大结果数
    search_wrapper = DuckDuckGoSearchAPIWrapper(region="cn-zh", max_results=3)
    web_search = DuckDuckGoSearchResults(api_wrapper=search_wrapper)
    
    # 为网络搜索设计的 Prompt 
    web_prompt_template = """
    你是华南理工大学校园百事通的智能助手。关于用户的问题，本地数据库中没有找到记录。
    以下是通过互联网搜索引擎检索到的最新相关信息。
    
    请根据这些网络信息，综合、客观地回答用户的问题。
    注意，你只回答与华南理工大学有关的问题，因为你是专门为华南理工大学服务的智能助手。不要回答与华南理工大学无关的内容。
    如果网络信息中依然无法找到答案，请回答："抱歉，通过本地库和全网搜索，未能找到相关确切信息。"

    网络搜索结果：
    {context}

    问题：{question}

    回答：
    """
    web_prompt = ChatPromptTemplate.from_template(web_prompt_template)
    print("搜索引擎初始化完成")

    rewrite_prompt_template = """
    你是一个搜索引擎关键词提取专家。你的任务是将用户的口语化提问转化为最高效的搜索关键词。
    
    规则：
    1. 必须包含“华南理工大学”。
    2. 如果用户是在询问一个集合或完整名单（例如“有哪些学院”、“有什么专业”、“几个校区”），请在关键词末尾加上“列表”、“汇总”或“设置”等词。
    3. 如果用户问如何做某件事（例如“怎么申请入学”、“如何预约图书馆座位”），请在关键词末尾加上“方法”、“步骤”或“指南”等词。
    3. 去掉代词和疑问词，只返回关键词本身，用空格分隔。

    用户提问：{question}
    搜索关键词：
        """
    rewrite_prompt = ChatPromptTemplate.from_template(rewrite_prompt_template)

    # 9. 构建新的RAG链
    def format_docs(docs):
        return "\n\n".join(doc.page_content for doc in docs)
    
    def rag_chain(question):
        total_start_time = time.time()
        
        docs = retriever.invoke(question)
        context = format_docs(docs)
        messages = prompt.format_messages(context=context, question=question)
        response = llm.invoke(messages)
        
        # 触发搜索引擎
        # 检查大模型是否回复了我们设定的找不到信息的固定话术
            
        if "抱歉，我在数据库中没有找到相关信息" in response.content:
            print("\n[INFO] 🚨 本地知识库未命中，正在请大模型提炼搜索关键词...")
            
            # 1. 调用大模型重写查询词 (Query Rewrite)
            rewrite_start = time.time()
            rewrite_messages = rewrite_prompt.format_messages(question=question)
            # 这里复用我们已经初始化好的 llm，它只生成几个词，速度极快
            rewrite_response = llm.invoke(rewrite_messages) 
            
            # 拿到大模型生成的关键词，并清理首尾可能多余的空格或换行
            search_query = rewrite_response.content.strip() 
            
            if DEBUG_MODE:
                print(f"[DEBUG] 大模型提取词耗时: {time.time()-rewrite_start:.2f}s")
                print(f"[DEBUG] 实际发送给搜索引擎的查询词: 🔍 [{search_query}]")
            
            # 2. 调用搜索引擎获取结果 (使用大模型提炼后的词)
            web_search_start = time.time()
            try:
                web_context = web_search.run(search_query) 
            except Exception as e:
                web_context = f"搜索引擎调用失败: {e}"
            
            if DEBUG_MODE:
                print(f"[DEBUG] 网络搜索耗时: {time.time()-web_search_start:.2f}s")
                print(f"[DEBUG] 搜索返回的原始数据:\n{web_context}")
            
            # 3. 构建新的基于网络的提示词（注意：回答时依然使用用户最原始的 question）
            web_messages = web_prompt.format_messages(context=web_context, question=question)
            
            # 4. 再次调用大模型生成最终答案
            web_response = llm.invoke(web_messages)
            
            return f"🌐【联网搜索结果】\n{web_response.content}"
        
        # 如果本地库直接命中了，就返回本地的答案
        if DEBUG_MODE:
            print(f"\n[DEBUG] 检索阶段完成，耗时: {time.time() - total_start_time:.2f}s")
            
        return f"📚【本地知识库】\n{response.content}"
    
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
    print("校园百事通 - 智能问答系统（终端版）")
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
