package cn.edu.nynu.codelab.auth.service.impl;

import cn.edu.nynu.codelab.auth.service.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的登录限流实现（生产推荐）。
 *
 * <p>策略：同一 IP + username 连续失败 5 次后锁定 10 分钟。
 *
 * <p>Key 设计：
 * <ul>
 *   <li>失败计数：{@code codelab:auth:login-attempt:{ip}:{username}} → 失败次数</li>
 *   <li>锁定标记：{@code codelab:auth:login-lock:{ip}:{username}} → "1"，带 TTL</li>
 * </ul>
 *
 * <p>登录成功后清理所有相关 key。多实例共享 Redis，限流全局有效。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.auth", name = "redis-enabled", havingValue = "true")
public class RedisLoginAttemptService implements LoginAttemptService {

    private static final String ATTEMPT_KEY_PREFIX = "codelab:auth:login-attempt:";
    private static final String LOCK_KEY_PREFIX = "codelab:auth:login-lock:";

    /** 最大失败次数 */
    private static final int MAX_ATTEMPTS = 5;
    /** 锁定时长（秒）：10 分钟 */
    private static final long LOCK_DURATION_SECONDS = 10 * 60L;

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        log.info("Redis 登录限流已启用: maxAttempts={}, lockDuration={}秒", MAX_ATTEMPTS, LOCK_DURATION_SECONDS);
    }

    @Override
    public void recordFailure(String ip, String username) {
        String attemptKey = buildAttemptKey(ip, username);
        String lockKey = buildLockKey(ip, username);

        // 原子递增计数
        Long count = redisTemplate.opsForValue().increment(attemptKey);
        // 设置过期时间（锁定时长），避免 key 永久残留
        redisTemplate.expire(attemptKey, LOCK_DURATION_SECONDS, TimeUnit.SECONDS);

        if (count != null && count >= MAX_ATTEMPTS) {
            // 触发锁定
            redisTemplate.opsForValue().set(lockKey, "1", LOCK_DURATION_SECONDS, TimeUnit.SECONDS);
            log.warn("登录限流触发: ip={}, username={}, count={}, 锁定{}秒", ip, username, count, LOCK_DURATION_SECONDS);
        }
    }

    @Override
    public void clearAttempts(String ip, String username) {
        String attemptKey = buildAttemptKey(ip, username);
        String lockKey = buildLockKey(ip, username);
        redisTemplate.delete(attemptKey);
        redisTemplate.delete(lockKey);
        log.debug("登录成功，清理限流计数: ip={}, username={}", ip, username);
    }

    @Override
    public boolean isBlocked(String ip, String username) {
        String lockKey = buildLockKey(ip, username);
        Boolean exists = redisTemplate.hasKey(lockKey);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public long getRemainingBlockSeconds(String ip, String username) {
        String lockKey = buildLockKey(ip, username);
        Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return (ttl != null && ttl > 0) ? ttl : 0;
    }

    private String buildAttemptKey(String ip, String username) {
        return ATTEMPT_KEY_PREFIX + ip + ":" + (username != null ? username.toLowerCase() : "");
    }

    private String buildLockKey(String ip, String username) {
        return LOCK_KEY_PREFIX + ip + ":" + (username != null ? username.toLowerCase() : "");
    }
}
