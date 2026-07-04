package com.genie.service.impl;

import com.genie.constant.RedisConstant;
import com.genie.exception.AccountLockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginAttemptService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 检查账号是否被锁定
     */
    public void checkLocked(String username) {
        String lockKey = RedisConstant.LOGIN_LOCK_PREFIX + username;
        Boolean isLocked = redisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(isLocked)) {
            Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
            throw new AccountLockedException("账号已被锁定，请 " + ttl + " 秒后再试");
        }
    }

    /**
     * 记录登录失败，返回剩余尝试次数
     */
    public long recordFailure(String username) {
        String failKey = RedisConstant.LOGIN_FAIL_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(failKey, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(failKey, RedisConstant.LOGIN_LOCK_TTL, TimeUnit.SECONDS);
        }
        long current = count != null ? count : 0;
        log.warn("用户 {} 登录失败，累计失败次数：{}", username, current);

        if (current >= RedisConstant.LOGIN_FAIL_MAX) {
            lockAccount(username);
            return 0;
        }
        return RedisConstant.LOGIN_FAIL_MAX - current;
    }

    private void lockAccount(String username) {
        String lockKey = RedisConstant.LOGIN_LOCK_PREFIX + username;
        redisTemplate.opsForValue().set(lockKey, "locked", RedisConstant.LOGIN_LOCK_TTL, TimeUnit.SECONDS);
        redisTemplate.delete(RedisConstant.LOGIN_FAIL_PREFIX + username);
        log.warn("账号 {} 已被锁定15分钟", username);
    }

    /**
     * 清除登录失败记录（登录成功后调用）
     */
    public void clearFailure(String username) {
        String failKey = RedisConstant.LOGIN_FAIL_PREFIX + username;
        redisTemplate.delete(failKey);
        log.debug("用户 {} 登录成功，清除失败记录", username);
    }
}