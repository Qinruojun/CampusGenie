package com.genie.context;

/**
 * 线程上下文，用于存储当前请求线程的用户信息
 * 通常与 JWT 拦截器配合使用，将解析出的用户 ID 存入当前线程
 * 使用 ThreadLocal 确保线程安全，请求结束后必须调用 remove 方法清除
 */
public class BaseContext {

    /**
     * 存储当前用户信息上下文
     */
    private static final ThreadLocal<UserContext> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前用户信息上下文
     * @param userContent 用户上下文
     */
    public static void setCurrentUserContext(UserContext userContent) {
        threadLocal.set(userContent);
    }

    /**
     * 获取当前用户 信息上下文
     * @return 用户信息上下文
     */
    public static UserContext getCurrentUserContext() {
        return threadLocal.get();
    }

    /**
     * 清除当前线程中的用户 信息上下文（必须在请求结束时调用，防止内存泄漏）
     */
    public static void removeCurrentUserContent() {
        threadLocal.remove();
    }
    public static Long getCurrentUserId() {
        UserContext user = threadLocal.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUsername() {
        UserContext user = threadLocal.get();
        return user != null ? user.getUsername() : null;
    }
    public static Integer getCurrentRole() {
        UserContext user = threadLocal.get();
        return user != null ? user.getRole() : null;
    }
}