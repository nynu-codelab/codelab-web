package cn.edu.nynu.codelab.auth.service.impl;

import cn.edu.nynu.codelab.auth.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的 Token 黑名单实现（生产推荐）。
 *
 * <p>设计：
 * <ul>
 *   <li>Key: {@code codelab:auth:blacklist:{sha256(token)}}</li>
 *   <li>Value: "1"（占位标记）</li>
 *   <li>TTL: 与 token 剩余有效期对齐，到期自动清理</li>
 *   <li>不存储完整 token，仅保存 SHA-256 摘要，降低泄露风险</li>
 * </ul>
 *
 * <p>多实例部署安全：Redis 为共享存储，任一实例登出后所有实例生效。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.auth", name = "redis-enabled", havingValue = "true")
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private static final String KEY_PREFIX = "codelab:auth:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final long blacklistTtlMs;

    public RedisTokenBlacklistService(
            StringRedisTemplate redisTemplate,
            @Value("${sa-token.timeout:604800}") long tokenTimeoutSeconds) {
        this.redisTemplate = redisTemplate;
        this.blacklistTtlMs = tokenTimeoutSeconds * 1000L;
        log.info("Redis Token 黑名单已启用，TTL={} 毫秒 ({} 天)", blacklistTtlMs, tokenTimeoutSeconds / 86400.0);
    }

    @Override
    public long getBlacklistTtlMs() {
        return blacklistTtlMs;
    }

    @Override
    public void blacklist(String token, long expireMs) {
        if (token == null || token.isEmpty()) {
            return;
        }
        String digest = sha256(token);
        String key = KEY_PREFIX + digest;
        long ttlMs = Math.max(expireMs, 60_000L); // 最短 1 分钟
        redisTemplate.opsForValue().set(key, "1", ttlMs, TimeUnit.MILLISECONDS);
        log.info("Token 已加入 Redis 黑名单: digest={}, ttlMs={}", digest, ttlMs);
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String digest = sha256(token);
        String key = KEY_PREFIX + digest;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public void cleanup() {
        // Redis 的 TTL 机制自动清理，无需手动维护
    }

    /**
     * 计算 token 的 SHA-256 摘要（十六进制小写）。
     */
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        }
    }
}
