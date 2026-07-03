package com.genie.service.impl;

import com.genie.constant.RedisConstant;
import com.genie.exception.RateLimitException;
import com.genie.service.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean isRateLimited(String keyPrefix, Long userId, long maxCount, long ttl) {
        String key = keyPrefix + userId;

        try {
            Long currentCount = redisTemplate.opsForValue().increment(key, 1);

            if (currentCount != null && currentCount == 1) {
                // 只有使用固定 TTL 时才设置
                if (ttl > 0) {
                    redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
                }
                // 如果 ttl == 0，不设置过期时间（由自然天逻辑控制）
                log.debug("限流计数器创建，key={}", key);
            }

            boolean limited = currentCount != null && currentCount > maxCount;
            if (limited) {
                log.warn("用户 {} 触发限流，当前次数={}，最大次数={}", userId, currentCount, maxCount);
            }
            return limited;

        } catch (Exception e) {
            log.error("Redis 限流检查失败，降级放行", e);
            return false;
        }
    }

    @Override
    public boolean check1MinLimit(Long userId) {
        return isRateLimited(
                RedisConstant.RATE_CONTRIBUTE_1MIN,
                userId,
                RedisConstant.RATE_1MIN_MAX,
                RedisConstant.RATE_1MIN_TTL
        );
    }

    @Override
    public boolean checkDayLimit(Long userId) {
        // 自然天限流：Key 中包含当天日期，每天自动重置
        String today = LocalDate.now().toString();
        String key = RedisConstant.RATE_CONTRIBUTE_DAY + today + ":" + userId;
        
        try {
            Long currentCount = redisTemplate.opsForValue().increment(key, 1);

            if (currentCount != null && currentCount == 1) {
                // 设置过期时间到当天 23:59:59
                long secondsToMidnight = getSecondsToMidnight();
                redisTemplate.expire(key, secondsToMidnight, TimeUnit.SECONDS);
                log.debug("自然天限流计数器创建，key={}, 今日剩余秒数={}", key, secondsToMidnight);
            }

            boolean limited = currentCount != null && currentCount > RedisConstant.RATE_DAY_MAX;
            if (limited) {
                log.warn("用户 {} 触发自然天限流，当前次数={}，最大次数={}", 
                    userId, currentCount, RedisConstant.RATE_DAY_MAX);
            }
            return limited;

        } catch (Exception e) {
            log.error("Redis 限流检查失败，降级放行", e);
            return false;
        }
    }

    /**
     * 计算到当天 23:59:59 的剩余秒数
     */
    private long getSecondsToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    @Override
    public void checkContributeLimit(Long userId) {
        if (check1MinLimit(userId)) {
            throw new RateLimitException("提交过于频繁，请 1 分钟后再试");
        }
        if (checkDayLimit(userId)) {
            throw new RateLimitException("今日提交次数已达上限（5次），请明天再试");
        }
    }
}