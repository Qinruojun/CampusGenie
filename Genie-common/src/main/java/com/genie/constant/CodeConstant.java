package com.genie.constant;

/**
 * 返回码常量
 */
public class CodeConstant {

    // 成功
    public static final Integer SUCCESS = 200;
    // 失败
    public static final Integer FAIL = 500;
    // 参数错误
    public static final Integer BAD_REQUEST = 400;
    // 未授权
    public static final Integer UNAUTHORIZED = 401;
    // 禁止访问
    public static final Integer FORBIDDEN = 403;
    // 资源不存在
    public static final Integer NOT_FOUND = 404;
    // 业务错误（如用户名重复等）
    public static final Integer BUSINESS_ERROR = 1001;
}