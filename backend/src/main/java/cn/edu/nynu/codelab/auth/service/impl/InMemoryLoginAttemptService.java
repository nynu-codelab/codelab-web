package cn.edu.nynu.codelab.auth.service.impl;

import cn.edu.nynu.codelab.auth.service.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的登录限流实现。
 * <p>
 * <b>策略：</b>同一 IP + username 连续失败 5 次后锁定 10 分钟。
 * 成功登录后清理失败计数。
 * </p>
 * <p>
 * <b>局限（生产风险）：</b>
 * <ul>
 *   <li>服务重启后所有计数丢失，限流重置</li>
 *   <li>多实例部署不共享计数，恶意请求可轮询不同实例绕过</li>
 *   <li>IP 可通过代理更换，仅作为基础防护</li>
 * </ul>
 * <b>生产建议：</b>替换为基于 Redis 的实现，结合滑动窗口算法，并可叠加验证码机制。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.auth", name = "redis-enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryLoginAttemptService implements LoginAttemptService {

    /** 最大失败次数 */
    private static final int MAX_ATTEMPTS = 5;
    /** 锁定时长（毫秒）：10 分钟 */
    private static final long LOCK_DURATION_MS = 10 * 60 * 1000L;

    /**
     * key: "ip:username" → 记录（失败次数 + 首次失败时间 + 锁定截止时间）
     */
    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    @Override
    public void recordFailure(String ip, String username) {
        String key = buildKey(ip, username);
        long now = System.currentTimeMillis();

        attempts.compute(key, (k, record) -> {
            if (record == null) {
                return new AttemptRecord(1, now, 0);
            }
            // 如果上次锁定已过期，重新计数
            if (record.lockedUntil > 0 && now > record.lockedUntil) {
                return new AttemptRecord(1, now, 0);
            }
            record.count++;
            if (record.count >= MAX_ATTEMPTS) {
                record.lockedUntil = now + LOCK_DURATION_MS;
                log.warn("登录限流触发: key={}, 锁定至 {}", key, record.lockedUntil);
            }
            return record;
        });
    }

    @Override
    public void clearAttempts(String ip, String username) {
        attempts.remove(buildKey(ip, username));
    }

    @Override
    public boolean isBlocked(String ip, String username) {
        AttemptRecord record = attempts.get(buildKey(ip, username));
        if (record == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (record.lockedUntil > 0 && now < record.lockedUntil) {
            return true;
        }
        // 过期锁定，清理
        if (record.lockedUntil > 0 && now >= record.lockedUntil) {
            attempts.remove(buildKey(ip, username));
        }
        return false;
    }

    @Override
    public long getRemainingBlockSeconds(String ip, String username) {
        AttemptRecord record = attempts.get(buildKey(ip, username));
        if (record == null || record.lockedUntil <= 0) {
            return 0;
        }
        long remaining = record.lockedUntil - System.currentTimeMillis();
        return remaining > 0 ? (long) Math.ceil(remaining / 1000.0) : 0;
    }

    private String buildKey(String ip, String username) {
        return ip + ":" + (username != null ? username.toLowerCase() : "");
    }

    /**
     * 登录尝试记录。
     */
    private static class AttemptRecord {
        int count;
        long firstAttemptTime;
        long lockedUntil;

        AttemptRecord(int count, long firstAttemptTime, long lockedUntil) {
            this.count = count;
            this.firstAttemptTime = firstAttemptTime;
            this.lockedUntil = lockedUntil;
        }
    }

}
