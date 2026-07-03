package com.genie.constant;

/**
 * Redis 缓存键常量
 */
public class RedisConstant {

    /**
     * 热点问题排行榜缓存
     */
    public static final String HOT_QUESTIONS = "hot:questions";

    public static final long HOT_QUESTIONS_TTL = 20;

}