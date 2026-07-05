import time
import traceback
from contextlib import asynccontextmanager
from typing import Any, Optional

import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from RAG_qa import answer_question, initialize_rag_system


# 本文件是 HTTP 接口层，只处理 FastAPI 相关逻辑：
# 1. 服务启动时初始化 RAG 系统
# 2. 接收前端传来的 question 和 session_id
# 3. 调用 RAG 服务得到回答
# 4. 把内部返回格式转换成前端需要的 JSON


class ChatRequest(BaseModel):
    """前端请求体。session_id 用来区分不同用户或不同聊天窗口。"""

    question: str = Field(..., min_length=1, description="用户问题")
    session_id: str = Field(default="default", description="会话 ID，同一会话会共享上下文记忆")


class ChatResponse(BaseModel):
    """前端响应体。related_questions 是相似度最高的推荐问题。"""

    answer: str
    cost_time: float
    session_id: str
    knowledge_id: Optional[int] = None
    related_questions: list[str] = Field(default_factory=list)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """FastAPI 生命周期：应用启动时只初始化一次模型和向量库。"""
    print("正在启动后端服务，初始化大模型和向量库...")
    app.state.rag_chain = initialize_rag_system()
    print("后端服务启动完毕，可以接收请求！")
    try:
        yield
    finally:
        app.state.rag_chain = None


app = FastAPI(
    title="校园百事通 API",
    description="基于 RAG 和外部搜索的问答接口",
    lifespan=lifespan,
)


def _normalize_answer_text(answer: Any) -> str:
    """给前端返回纯答案文本，去掉内部来源标签和多余空白。"""
    text = str(answer or "").strip()
    source_prefixes = [
        "📚【本地知识库】",
        "【本地知识库】",
        "【全网 AI 搜索总结】",
        "【全网AI搜索总结】",
    ]
    for prefix in source_prefixes:
        if text.startswith(prefix):
            text = text[len(prefix) :].strip()
            break
    return text


def _build_chat_response(answer: Any, cost_time: float, session_id: str) -> ChatResponse:
    """把 RAG 层返回的 dict 或字符串统一转成 ChatResponse。"""
    if isinstance(answer, dict):
        return ChatResponse(
            answer=_normalize_answer_text(answer.get("answer", "")),
            cost_time=cost_time,
            session_id=session_id,
            knowledge_id=answer.get("kb_id"),
            related_questions=answer.get("related_questions", []),
        )

    return ChatResponse(
        answer=_normalize_answer_text(answer),
        cost_time=cost_time,
        session_id=session_id,
    )


@app.get("/health")
async def health_check():
    """健康检查接口，用于确认服务是否已经初始化完成。"""
    return {
        "status": "ok" if getattr(app.state, "rag_chain", None) else "initializing",
    }


@app.post("/api/qa/ask", response_model=ChatResponse)
async def chat_endpoint(request: ChatRequest):
    """问答接口：同一个 session_id 下的问题会共享最近几轮上下文。"""
    rag_chain = getattr(app.state, "rag_chain", None)
    if rag_chain is None:
        raise HTTPException(status_code=503, detail="系统尚未初始化完成")

    question = request.question.strip()
    if not question:
        raise HTTPException(status_code=400, detail="问题不能为空")
    session_id = request.session_id.strip() or "default"

    start_time = time.time()
    try:
        answer = answer_question(question, rag_chain, session_id=session_id)
        if isinstance(answer, str) and answer.startswith("回答出错："):
            raise RuntimeError(answer)

        print(f"[DEBUG] Python RAG 返回结果: {answer}")
        return _build_chat_response(answer, round(time.time() - start_time, 2), session_id)
    except Exception as exc:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"推理出错: {exc}") from exc


if __name__ == "__main__":
    uvicorn.run("api_qa_ask:app", host="0.0.0.0", port=8000, reload=True)
