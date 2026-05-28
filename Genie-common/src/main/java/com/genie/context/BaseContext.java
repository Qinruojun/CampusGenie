package com.genie.context;

/**
 * 线程上下文，用于存储当前请求线程的用户信息
 * 通常与 JWT 拦截器配合使用，将解析出的用户 ID 存入当前线程
 * 使用 ThreadLocal 确保线程安全，请求结束后必须调用 remove 方法清除
 */
public class BaseContext {

    /**
     * 存储当前用户的 ID
     */
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     * @param id 用户ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前用户 ID
     * @return 用户ID，可能为 null
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 清除当前线程中的用户 ID（必须在请求结束时调用，防止内存泄漏）
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}