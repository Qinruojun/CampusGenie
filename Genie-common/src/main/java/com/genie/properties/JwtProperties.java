//package com.genie.properties;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
///**
// * JWT 令牌配置
// */
//@Component
//@ConfigurationProperties(prefix = "genie.jwt")
//@Data
//public class JwtProperties {
//
//    /**
//     * 管理员端 JWT 密钥
//     */
//    private String adminSecretKey;
//
//    /**
//     * 管理员端 JWT 有效期（毫秒）
//     */
//    private Long adminTtl;
//
//    /**
//     * 管理员端 Token 名称（放入 Header 的 key）
//     */
//    private String adminTokenName;
//
//    /**
//     * 普通用户端 JWT 密钥
//     */
//    private String userSecretKey;
//
//    /**
//     * 普通用户端 JWT 有效期（毫秒）
//     */
//    private Long userTtl;
//
//    /**
//     * 普通用户端 Token 名称
//     */
//    private String userTokenName;
//}