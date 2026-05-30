package com.genie.constant;

/**
 * 返回码常量
 * 定义系统统一的业务状态码
 */
public class CodeConstant {

    // ==================== 成功 ====================
    /** 请求成功 */
    public static final Integer SUCCESS = 200;

    // ==================== 客户端错误 ====================
    /** 参数错误（如字段校验不通过） */
    public static final Integer BAD_REQUEST = 400;

    /** 未授权（未登录或 Token 失效） */
    public static final Integer UNAUTHORIZED = 401;

    /** 禁止访问（无权限） */
    public static final Integer FORBIDDEN = 403;

    /** 资源不存在 */
    public static final Integer NOT_FOUND = 404;

    /** 业务冲突（如重复提交、状态不允许） */
    public static final Integer CONFLICT = 409;

    // ==================== 服务端错误 ====================
    /** 系统内部错误（未预期的异常） */
    public static final Integer INTERNAL_SERVER_ERROR = 500;


}