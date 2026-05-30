package com.genie.exception;

// 密码错误
public class PasswordErrorException extends BaseException {
    public PasswordErrorException() {}
    public PasswordErrorException(String msg) { super(msg); }
}
