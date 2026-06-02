package com.genie.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private Integer code=0;
    public BaseException() {
        super();
    }
    public BaseException(String message) {
        super(message);
    }
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}