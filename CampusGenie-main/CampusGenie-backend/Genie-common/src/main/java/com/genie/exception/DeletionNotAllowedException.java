package com.genie.exception;

// 删除不允许（例如分类下还有知识）
public class DeletionNotAllowedException extends BaseException {
    public DeletionNotAllowedException() {}
    public DeletionNotAllowedException(String msg) { super(msg); }
}
