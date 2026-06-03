# pc_proxy.py
import os
import requests
from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel

app = FastAPI(title="Local Intent API Proxy")

# 你的 SSH 隧道入口
B_INTENT_URL = "http://127.0.0.1:18000/intent"

# 简单鉴权 token，建议后面改成环境变量
API_TOKEN = os.getenv("INTENT_PROXY_TOKEN", "122333444455555")


class QuestionRequest(BaseModel):
    question: str


@app.get("/health")
def health():
    return {
        "status": "ok",
        "target": B_INTENT_URL,
    }


@app.post("/classify")
def classify_intent(
    req: QuestionRequest,
    authorization: str | None = Header(default=None),
):
    # 简单鉴权，避免别人随便调用你的服务
    expected = f"Bearer {API_TOKEN}"
    if authorization != expected:
        raise HTTPException(status_code=401, detail="Unauthorized")

    try:
        resp = requests.post(
            B_INTENT_URL,
            json={"question": req.question},
            timeout=60,
        )
        resp.raise_for_status()
        result = resp.json()
    except Exception as e:
        raise HTTPException(
            status_code=502, detail=f"Backend intent service error: {e}")

    # 兼容你同学文档里的返回格式
    if result.get("status") == "success":
        category_id = result.get("intent_id")
    else:
        category_id = None

    return {
        "category_id": category_id,
        "confidence": result.get("confidence", 0),
        "intent_id": result.get("intent_id"),
        "intent_code": result.get("intent_code"),
        "intent_name": result.get("intent_name"),
        "status": result.get("status"),
        "raw": result,
    }
