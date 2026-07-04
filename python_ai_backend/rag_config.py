import os
from dataclasses import dataclass, field
from pathlib import Path

from dotenv import load_dotenv


# 本文件只负责“配置从哪里来”：
# 1. 设置 HuggingFace / transformers 运行环境
# 2. 读取 .env 和系统环境变量
# 3. 把数据库、意图识别、RAG 参数集中放到 dataclass 里


def _env_bool(name: str, default: bool) -> bool:
    """把环境变量里的字符串转换成布尔值。"""
    value = os.getenv(name)
    if value is None:
        return default
    return value.strip().lower() in {"1", "true", "yes", "on"}


def setup_environment() -> None:
    """初始化模型相关环境变量，并加载 .env 文件。"""
    os.environ.setdefault("HF_ENDPOINT", "https://hf-mirror.com")
    os.environ.setdefault("TRANSFORMERS_OFFLINE", "0")
    os.environ.setdefault("TOKENIZERS_PARALLELISM", "false")
    os.environ.setdefault("HF_HUB_DISABLE_SYMLINKS_WARNING", "1")
    load_dotenv()


setup_environment()

SCRIPT_DIR = Path(__file__).resolve().parent
DEBUG_MODE = _env_bool("RAG_DEBUG", True)


@dataclass(frozen=True)
class DatabaseConfig:
    """知识库 MySQL 连接配置。优先读取环境变量，未设置时使用默认值。"""

    host: str = field(default_factory=lambda: os.getenv("RAG_DB_HOST", "8.148.158.43"))
    user: str = field(default_factory=lambda: os.getenv("RAG_DB_USER", "dev"))
    password: str = field(default_factory=lambda: os.getenv("RAG_DB_PASSWORD", "Campus@Dev#DB123456"))
    database: str = field(default_factory=lambda: os.getenv("RAG_DB_NAME", "CampusGenie"))
    charset: str = field(default_factory=lambda: os.getenv("RAG_DB_CHARSET", "utf8mb4"))


@dataclass(frozen=True)
class IntentConfig:
    """意图识别服务配置，用于按分类过滤知识库检索范围。"""

    url: str = field(default_factory=lambda: os.getenv("INTENT_CLASSIFY_URL", "http://127.0.0.1:8088/classify"))
    token: str = field(default_factory=lambda: os.getenv("INTENT_PROXY_TOKEN", "122333444455555"))
    timeout: float = field(default_factory=lambda: float(os.getenv("INTENT_CLASSIFY_TIMEOUT", "0.5")))


@dataclass(frozen=True)
class RagConfig:
    """RAG 系统的总配置，聚合模型、向量库、检索和兜底搜索参数。"""

    # 旧字段保留：如果后续要从本地文本构建知识库，可继续使用。
    text_file_path: str = str(SCRIPT_DIR / "1_classification_log.json")
    chunk_size: int = 300
    chunk_overlap: int = 40

    # 向量化模型：把问题和知识库内容编码成向量，供 FAISS 相似度检索。
    embedding_model: str = "BAAI/bge-small-zh-v1.5"

    # 大模型配置：ChatOpenAI 兼容接口，可以接本地或远程 OpenAI 格式服务。
    model_name: str = field(default_factory=lambda: os.getenv("RAG_LLM_MODEL", "qwen"))
    llm_api_base: str = field(default_factory=lambda: os.getenv("RAG_LLM_API_BASE", "http://116.62.139.249:8000/v1"))
    llm_api_key: str = field(default_factory=lambda: os.getenv("RAG_LLM_API_KEY", "not-needed"))
    temperature: float = 0.1
    max_tokens: int = 800

    # 向量库路径：首次启动会创建，后续启动会复用已有索引。
    vectorstore_path: str = field(
        default_factory=lambda: os.getenv(
            "RAG_VECTORSTORE_PATH",
            str(Path.home() / ".scut_rag_vector" / "faiss_index"),
        )
    )

    # 检索参数：k 表示候选数量，distance_threshold 越小表示要求越相似。
    retrieval_k: int = field(default_factory=lambda: int(os.getenv("RAG_RETRIEVAL_K", "3")))
    distance_threshold: float = field(default_factory=lambda: float(os.getenv("RAG_DISTANCE_THRESHOLD", "1.1")))
    memory_rounds: int = field(default_factory=lambda: int(os.getenv("RAG_MEMORY_ROUNDS", "5")))

    # 本地知识库没有命中时，调用博查 AI Search 作为兜底。
    bocha_api_url: str = "https://api.bochaai.com/v1/ai-search"
    bocha_model: str = field(default_factory=lambda: os.getenv("BOCHA_MODEL", "bocha-glm-4"))
    bocha_timeout: float = field(default_factory=lambda: float(os.getenv("BOCHA_TIMEOUT", "15")))
    database: DatabaseConfig = field(default_factory=DatabaseConfig)
    intent: IntentConfig = field(default_factory=IntentConfig)


def load_rag_config() -> RagConfig:
    """统一的配置加载入口，其他模块不要直接拼散配置。"""
    return RagConfig()
