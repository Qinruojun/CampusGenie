package com.genie.service;

import com.genie.exception.RateLimitException;

/**
 * 限流服务接口
 */
public interface RateLimitService {

    /**
     * 检查是否超过限流
     *
     * @param keyPrefix Key 前缀
     * @param userId    用户 ID
     * @param maxCount  最大允许次数
     * @param ttl       时间窗口（秒）
     * @return true-超过限制（被限流），false-未超过限制
     */
    boolean isRateLimited(String keyPrefix, Long userId, long maxCount, long ttl);

    /**
     * 检查 1 分钟限流（1次/分钟）
     */
    boolean check1MinLimit(Long userId);

    /**
     * 检查 24 小时限流（5次/24小时）
     */
    boolean checkDayLimit(Long userId);

    /**
     * 综合检查限流
     *
     * @param userId 用户 ID
     * @throws RateLimitException 如果触发限流
     */
    void checkContributeLimit(Long userId);
}