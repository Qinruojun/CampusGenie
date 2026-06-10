

    // ==================== 成功 ====================
    /** 请求成功 */
  export const SUCCESS = 200;

    // ==================== 客户端错误 ====================
    /** 参数错误（如字段校验不通过） */
    export const BAD_REQUEST = 400;

    /** 未授权（未登录或 Token 失效） */
    export const UNAUTHORIZED = 401;

    /** 禁止访问（无权限） */
    export  const constFORBIDDEN = 403;

    /** 资源不存在 */
    export const NOT_FOUND = 404;

    /** 业务冲突（如重复提交、状态不允许） */
    export const CONFLICT = 409;

    // ==================== 服务端错误 ====================
    /** 系统内部错误（未预期的异常） */
    export const INTERNAL_SERVER_ERROR = 500;
    /** 外部服务错误（如数据库、缓存等） */
    export const EXTERNAL_SERVER_ERROR = 502;


