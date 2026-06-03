# pc_intent_client.py
import json
import requests

URL = "http://127.0.0.1:18000/intent"


def ask(question: str):
    resp = requests.post(
        URL,
        json={"question": question},
        timeout=60,
    )
    resp.raise_for_status()
    return resp.json()


if __name__ == "__main__":
    print("输入问题，回车发送。输入 q 退出。")

    while True:
        question = input("> ").strip()
        if question.lower() in {"q", "quit", "exit"}:
            break
        if not question:
            continue

        try:
            result = ask(question)
            print(json.dumps(result, ensure_ascii=False, indent=2))
        except Exception as e:
            print(f"请求失败: {e}")
