package cn.edu.nynu.codelab.auth.service;

/**
 * Token 黑名单服务接口。
 * <p>
 * 在 Sa-Token JWT 模式下，普通 logout 无法真正让 token 失效（JWT 无状态）。
 * 通过黑名单机制，在登出时将 token 标记为失效，后续请求在拦截器中被拒绝。
 * </p>
 *
 * @author NYNU Code Lab
 */
public interface TokenBlacklistService {

    /**
     * 将 token 加入黑名单。
     *
     * @param token    待失效的 token 值
     * @param expireMs 黑名单有效期（毫秒），应不短于 token 剩余有效期
     */
    void blacklist(String token, long expireMs);

    /**
     * 检查 token 是否在黑名单中（已失效）。
     *
     * @param token token 值
     * @return true 表示 token 已失效
     */
    boolean isBlacklisted(String token);

    /**
     * 获取黑名单保留时长（毫秒）。
     * <p>
     * 该值应 ≥ token 最大有效期，确保已注销 token 不会在黑名单过期后重新有效。
     * </p>
     *
     * @return 黑名单保留时长（毫秒）
     */
    long getBlacklistTtlMs();

    /**
     * 清理过期的黑名单条目。
     */
    void cleanup();

}
