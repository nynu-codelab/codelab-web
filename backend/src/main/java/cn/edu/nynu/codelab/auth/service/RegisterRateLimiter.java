package cn.edu.nynu.codelab.auth.service;

import cn.edu.nynu.codelab.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册限流器——基于 IP 的简单内存限流。
 * <p>
 * 与 {@link LoginAttemptService} 不同，注册限流仅按 IP 维度（攻击者会轮换用户名），
 * 且不区分用户名。生产高并发场景可替换为 Redis 滑动窗口实现。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Component
public class RegisterRateLimiter {

    /** 同 IP 两次注册最小间隔（毫秒） */
    private static final long MIN_INTERVAL_MS = 30_000; // 30 秒

    /** 同 IP 一小时最大注册次数 */
    private static final int MAX_PER_HOUR = 5;
    /** 一小时窗口（毫秒） */
    private static final long HOUR_MS = 3_600_000;

    /** IP → 最近一次注册时间戳 */
    private final Map<String, Long> lastRegisterTime = new ConcurrentHashMap<>();
    /** IP → 一小时内注册次数 + 窗口起始时间 */
    private final Map<String, HourWindow> hourWindowMap = new ConcurrentHashMap<>();

    /**
     * 检查 IP 是否允许注册。若被限流则抛出异常。
     *
     * @param ip 客户端 IP
     * @throws RuntimeException 限流提示
     */
    public void checkAllowed(String ip) {
        long now = System.currentTimeMillis();

        // 1. 最小间隔检查
        Long lastTime = lastRegisterTime.get(ip);
        if (lastTime != null && (now - lastTime) < MIN_INTERVAL_MS) {
            long waitSeconds = (MIN_INTERVAL_MS - (now - lastTime)) / 1000 + 1;
            log.warn("注册频率过高（间隔限制），IP: {}", ip);
            throw new BusinessException("注册过于频繁，请 " + waitSeconds + " 秒后再试");
        }

        // 2. 小时窗口计数检查
        HourWindow window = hourWindowMap.computeIfAbsent(ip, k -> new HourWindow(now));
        synchronized (window) {
            if (now - window.startTime > HOUR_MS) {
                // 窗口过期，重置
                window.startTime = now;
                window.count = 0;
            }
            if (window.count >= MAX_PER_HOUR) {
                long waitMinutes = (HOUR_MS - (now - window.startTime)) / 60_000 + 1;
                log.warn("注册频率过高（小时限制），IP: {}, 已有 {} 次", ip, window.count);
                throw new BusinessException("注册过于频繁，请 " + waitMinutes + " 分钟后再试");
            }
        }
    }

    /**
     * 注册成功后记录本次注册。
     *
     * @param ip 客户端 IP
     */
    public void recordSuccess(String ip) {
        long now = System.currentTimeMillis();
        lastRegisterTime.put(ip, now);
        HourWindow window = hourWindowMap.computeIfAbsent(ip, k -> new HourWindow(now));
        synchronized (window) {
            if (now - window.startTime > HOUR_MS) {
                window.startTime = now;
                window.count = 0;
            }
            window.count++;
        }
    }

    /**
     * 一小时窗口记录。
     */
    private static class HourWindow {
        long startTime;
        int count;

        HourWindow(long startTime) {
            this.startTime = startTime;
            this.count = 0;
        }
    }
}
