package com.genie.constant;

/**
 * 目标类型常量
 * 用于 admin_log和review_log 表的 target_type 字段
 */
public class TargetTypeConstant {

    /** 知识库（正式知识条目） */
    public static final String KNOWLEDGE_BASE = "knowledge_base";

    /** 知识草稿 */
    public static final String KNOWLEDGE_DRAFT = "knowledge_draft";

    /** 分类 */
    public static final String CATEGORY = "category";

    /** 用户贡献 */
    public static final String CONTRIBUTION = "contribution";
    //review_log记录  用户贡献为0 知识草稿为1
    public static final Integer Review_TYPE_CONTRIBUTION = 1;
    public static final Integer Review_TYPE_KNOWLEDGE_DRAFT = 2;
}