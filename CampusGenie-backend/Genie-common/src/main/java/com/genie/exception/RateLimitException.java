package com.genie.exception;

/**
 * 限流异常（提交过于频繁）
 */
public class RateLimitException extends BaseException {
    public RateLimitException() {}
    public RateLimitException(String msg) { super(msg); }
}