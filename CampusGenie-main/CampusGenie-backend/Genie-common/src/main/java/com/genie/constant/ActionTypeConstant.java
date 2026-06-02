package com.genie.constant;

/**
 * 操作类型常量
 * 用于 admin_log 表的 action_type 字段
 */
public class ActionTypeConstant {

    /** 新增 */
    public static final String INSERT = "INSERT";

    /** 修改 */
    public static final String UPDATE = "UPDATE";

    /** 删除 */
    public static final String DELETE = "DELETE";

    /** 审核 */
    public static final String REVIEW = "REVIEW";

    /** 停用 */
    public static final String DISABLE = "DISABLE";

    /** 启用 */
    public static final String ENABLE = "ENABLE";

    /** 批量导入 */
    public static final String BATCH_IMPORT = "BATCH_IMPORT";
}