package com.genie.exception;

// 登录失败
public class LoginFailedException extends BaseException {
    public LoginFailedException() {}
    public LoginFailedException(String msg) { super(msg); }
}