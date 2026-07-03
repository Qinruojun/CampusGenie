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


    // ==================== 用户贡献限流 ====================
    /** 用户贡献限流 Key 前缀（1分钟） */
    public static final String RATE_CONTRIBUTE_1MIN = "rate:contribute:1min:";
    /** 用户贡献限流 Key 前缀（24小时） */
    public static final String RATE_CONTRIBUTE_DAY = "rate:contribute:day:";


    /** 1分钟限流 TTL（秒） */
    public static final long RATE_1MIN_TTL = 60;
    /**自然天限流 TTL（秒） */
    public static final long RATE_DAY_TTL = 0;

    /** 1分钟内最大提交次数 */
    public static final long RATE_1MIN_MAX = 1;
    /** 24小时内最大提交次数 */
    public static final long RATE_DAY_MAX = 5;

    // ==================== 登录密码错误锁定 ====================
    /** 登录失败计数 Key 前缀 */
    public static final String LOGIN_FAIL_PREFIX = "login:fail:";
    /** 登录锁定标记 Key 前缀 */
    public static final String LOGIN_LOCK_PREFIX = "login:lock:";
    /** 最大允许失败次数 */
    public static final long LOGIN_FAIL_MAX = 5;
    /** 锁定时间（秒） */
    public static final long LOGIN_LOCK_TTL = 900;  // 15分钟


    // ==================== 待审核计数 ====================
    /** 待审核知识条目数量 */
    public static final String PENDING_REVIEW_COUNT = "pending:review:count";

}