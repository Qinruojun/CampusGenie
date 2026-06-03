package com.genie.constant;

/**
 * 消息提示常量
 */
public class MessageConstant {

    // 通用
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String ALREADY_EXISTS = "已存在";
    public static final String NOT_FOUND = "数据不存在";
    public static final String OPERATION_SUCCESS = "操作成功";
    public static final String OPERATION_FAILED = "操作失败";

    // 用户/管理员
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_LOCKED = "账号被禁用";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String PASSWORD_FAILED = "密码修改失败";
    //身份不正确
    public static final String IDENTITY_ERROR = "身份不匹配";

    // 知识库
    public static final String KNOWLEDGE_NOT_FOUND = "知识条目不存在";
    public static final String CATEGORY_BE_RELATED = "当前分类下存在知识条目，不能删除";
    public static final String QUESTION_ALREADY_EXISTS = "该问题已存在";

    // 用户贡献
    public static final String CONTRIBUTION_ALREADY_EXISTS = "该问题已有人提交，请等待审核";
    public static final String CONTRIBUTION_NOT_FOUND = "贡献内容不存在";
    public static final String CONTRIBUTION_ALREADY_REVIEWED = "该贡献已审核";

    // 审核
    public static final String REJECT_REASON_REQUIRED = "驳回理由不能为空";

    // 文件上传
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String FILE_FORMAT_ERROR = "文件格式错误，请上传 Excel 或 JSON 文件";


}