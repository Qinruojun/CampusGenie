import sys

from rag_config import DEBUG_MODE, load_rag_config
from rag_service import (
    BochaAISearchClient,
    IntentClassifierClient,
    KnowledgeRepository,
    ModelScopeEmbeddings,
    RagQAService,
    VectorStoreFactory,
    answer_question,
    initialize_rag_system,
)


# 本文件是兼容入口：
# - 真正的业务实现已经移动到 rag_service.py
# - 配置集中在 rag_config.py

CONFIG = load_rag_config()

# 明确导出的名字，方便其他模块知道 RAG_qa.py 对外提供哪些能力。
__all__ = [
    "BochaAISearchClient",
    "CONFIG",
    "DEBUG_MODE",
    "IntentClassifierClient",
    "KnowledgeRepository",
    "ModelScopeEmbeddings",
    "RagQAService",
    "VectorStoreFactory",
    "answer_question",
    "initialize_rag_system",
]


def main() -> None:
    """本地命令行调试入口，运行 python RAG_qa.py 后可直接输入问题测试。"""
    rag_chain = initialize_rag_system()

    print("\n" + "=" * 50)
    print("校园百事通")
    print("输入你的问题，输入 'exit' 或 'quit' 退出系统")
    print(f"调试模式: {'开启' if DEBUG_MODE else '关闭'}")
    print("=" * 50 + "\n")

    try:
        while True:
            user_input = input("请输入问题: ").strip()
            if user_input.lower() in {"exit", "quit", "退出"}:
                print("\n感谢使用，再见！")
                break

            if not user_input:
                continue

            print("\n正在思考...")
            answer = answer_question(user_input, rag_chain)
            print(f"\n回答: {answer}\n")
            print("-" * 50 + "\n")
    except KeyboardInterrupt:
        print("\n\n检测到中断信号，系统正在退出...")
        print("感谢使用，再见！")
        sys.exit(0)


if __name__ == "__main__":
    main()
