"""
/api/qa/ask 只是人为给这个功能起的一个“门牌号”。
只要前端发起请求的地址和后端定义的门牌号对得上，叫什么名字都可以！！！

将本地终端版的问答系统 (RAG_qa.py) 封装为标准的 RESTful API 接口， 以便前端网页进行网络调用

主要业务流程:
    1. 生命周期管理: 使用 @app.on_event("startup") 在服务器启动时全局加载一次 
       大模型和向量数据库，避免每次请求重复加载，大幅降低接口延迟。
    2. 数据校验模型: 借助 Pydantic 定义清晰的请求体 (ChatRequest) 和 响应体 (ChatResponse)。
    3. 核心问答接口 (POST /api/qa/ask): 接收用户提问，调用底层的 RAG 双层架构生成答案，
       并计算单次请求的推理耗时，最终以 JSON 格式返回给前端。
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn
import time

# 导入你写好的 RAG_qa.py 里的函数
from RAG_qa import initialize_rag_system, answer_question

# 1. 初始化 FastAPI 应用
app = FastAPI(title="校园百事通 API", description="基于 RAG 和外部搜索的问答接口")

# 全局变量存储 RAG 链
global_rag_chain = None

# 2. 定义前端传过来的数据格式
class ChatRequest(BaseModel):
    question: str
    # 后续可以加更多参数，比如 user_id, session_id 等
    # session_id: str = "default" 

# 3. 定义返回给前端的数据格式
class ChatResponse(BaseModel):
    answer: str
    cost_time: float

# 4. 在后端启动时，只执行一次初始化（加载大模型、向量库）
@app.on_event("startup")
async def startup_event():
    print("正在启动后端服务，初始化大模型和向量库...")
    global global_rag_chain
    global_rag_chain = initialize_rag_system()
    print("后端服务启动完毕，可以接收请求！")

# 5. 定义对外的聊天接口
@app.post("/api/qa/ask", response_model=ChatResponse)
async def chat_endpoint(request: ChatRequest):
    if not global_rag_chain:
        raise HTTPException(status_code=500, detail="系统尚未初始化完成")
    
    start_time = time.time()
    
    try:
        # 调用 qa.py 中的回答函数
        answer = answer_question(request.question, global_rag_chain)
        cost_time = round(time.time() - start_time, 2)
        
        return ChatResponse(answer=answer, cost_time=cost_time)
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"推理出错: {str(e)}")

# 6. 运行服务器
if __name__ == "__main__":
    # host="0.0.0.0" 允许外部网络访问，port 是端口号
<<<<<<< HEAD
    uvicorn.run("api_qa_ask:app", host="0.0.0.0", port=8000, reload=True)
=======
    uvicorn.run("api:app", host="0.0.0.0", port=8000, reload=True)
>>>>>>> eda670b118d65e13926874e1488fb3cef4e8c49f
