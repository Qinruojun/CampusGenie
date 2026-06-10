package com.genie.constant;

/**
 * 状态常量（启用/禁用）
 */
public class StatusConstant {

    /**
     * 启用
     */
    public static final Integer ENABLE = 1;

    /**
     * 禁用
     */
    public static final Integer DISABLE = 0;

    /**
     * 知识库发布状态
     */
    public static final Integer PUBLISHED = 1;
    public static final Integer STOPPED = 0;

    /**
     * 用户账号状态
     */
    public static final Integer USER_NORMAL = 1;
    public static final Integer USER_BANNED = 0;
    //审核表三种状态，待审核，通过，驳回
    public static final Integer WAIT_FOR_REVIEW = 0;
    public static final Integer REVIEW_PASS = 1;
    public static final Integer REVIEW_REJECT = 2;

}