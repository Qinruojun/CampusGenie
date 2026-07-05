package com.genie.exception;

import org.springframework.http.HttpStatus;

public class BizException extends RuntimeException {
  private final HttpStatus status;

  public BizException(String message) {
    this(HttpStatus.BAD_REQUEST, message);
  }

  public BizException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
