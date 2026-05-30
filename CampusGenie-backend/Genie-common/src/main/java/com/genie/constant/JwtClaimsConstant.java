package com.genie.constant;

/**
 * JWT Claims 常量,放到JWT payload 里面的信息，JWT payload不是加密的，不能放密码等信息
 */
public class JwtClaimsConstant {

    /**
     * 用户ID（管理员或普通用户）
     */
    public static final String USER_ID = "userId";

    /**
     * 用户名
     */
    public static final String USERNAME = "username";

    /**
     * 用户角色：用户还是管理员
     */
    public static final String ROLE = "role";


}