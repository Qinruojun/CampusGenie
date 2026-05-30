package com.genie.handler;

import com.genie.constant.MessageConstant;
import com.genie.exception.BaseException;
import com.genie.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，统一处理项目中抛出的各类异常，返回统一格式的 Result 响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常 BaseException
     * @param ex 业务异常
     * @return 错误结果
     */
    @ExceptionHandler
    public Result<Void> exceptionHandler(BaseException ex) {
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理 SQL 唯一约束冲突异常（如用户名重复）
     * @param ex SQL异常
     * @return 错误结果
     */
    @ExceptionHandler
    public Result<Void> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains("Duplicate entry")) {
            // 从异常信息中提取重复的值，例如：Duplicate entry 'admin' for key 'uk_username'
            String[] split = msg.split(" ");
            String duplicateValue = split[2];   // 获得重复的值，如 'admin'
            String errorMsg = duplicateValue + MessageConstant.ALREADY_EXISTS;
            return Result.error(errorMsg);
        } else {
            log.error("SQL异常：{}", msg);
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }


}