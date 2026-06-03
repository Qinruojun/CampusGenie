package com.genie.exception;

// 贡献重复提交
public class ContributionAlreadyExistsException extends BaseException {
    public ContributionAlreadyExistsException() {}
    public ContributionAlreadyExistsException(String msg) { super(msg); }
}
