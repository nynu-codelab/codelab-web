package cn.edu.nynu.codelab.auth.service.impl;

import cn.edu.nynu.codelab.auth.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的 Token 黑名单实现。
 * <p>
 * <b>局限（生产风险）：</b>
 * <ul>
 *   <li>服务重启后所有失效记录丢失，已登出的 token 重新变为有效</li>
 *   <li>多实例部署不共享黑名单，登出仅对当前实例生效</li>
 *   <li>内存占用随登出次数增长（有定期清理 + 过期自动剔除）</li>
 * </ul>
 * <b>生产建议：</b>替换为基于 Redis 的实现，使用 String 或 Set 结构存储黑名单。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.auth", name = "redis-enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    /**
     * token → 过期时间戳（毫秒）
     */
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 黑名单保留时长（毫秒）。
     * <p>
     * 来源：sa-token.timeout 配置（秒），默认 604800 秒 = 7 天。
     * 转换为毫秒后作为黑名单 TTL，确保：
     * <pre>
     *   token 有效期 = timeout 秒
     *   黑名单保留期 = timeout * 1000 毫秒 = token 有效期
     * </pre>
     * 不存在 token 在黑名单过期后重新有效的问题。
     * </p>
     */
    private final long blacklistTtlMs;

    public InMemoryTokenBlacklistService(
            @Value("${sa-token.timeout:604800}") long tokenTimeoutSeconds) {
        // tokenTimeoutSeconds 来自 sa-token.timeout 配置，单位：秒
        // 黑名单 TTL 对齐 token 有效期，确保已注销 token 不会在黑名单过期后重新有效
        this.blacklistTtlMs = tokenTimeoutSeconds * 1000L;
        log.info("Token 黑名单 TTL 初始化为 {} 毫秒 ({} 秒 = {} 天)",
                blacklistTtlMs, tokenTimeoutSeconds, tokenTimeoutSeconds / 86400.0);
    }

    /**
     * 获取黑名单保留时长（毫秒），供 AuthService 在调用 blacklist() 时使用。
     */
    public long getBlacklistTtlMs() {
        return blacklistTtlMs;
    }

    @Override
    public void blacklist(String token, long expireMs) {
        if (token == null || token.isEmpty()) {
            return;
        }
        long expireAt = System.currentTimeMillis() + Math.max(expireMs, 60_000L); // 最短 1 分钟
        blacklist.put(token, expireAt);
        log.info("Token 已加入黑名单，过期时间戳: {}", expireAt);
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Long expireAt = blacklist.get(token);
        if (expireAt == null) {
            return false;
        }
        if (System.currentTimeMillis() > expireAt) {
            // 惰性清理过期条目
            blacklist.remove(token, expireAt);
            return false;
        }
        return true;
    }

    @Override
    @Scheduled(fixedRate = 3600000) // 每小时清理一次过期条目
    public void cleanup() {
        long now = System.currentTimeMillis();
        int before = blacklist.size();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
        int removed = before - blacklist.size();
        if (removed > 0) {
            log.info("Token 黑名单过期清理: 移除 {} 条, 剩余 {} 条", removed, blacklist.size());
        }
    }

}
