package cn.edu.nynu.codelab.auth.service;

/**
 * 登录限流服务接口。
 * <p>
 * 按 IP + username 组合记录失败次数，连续失败达到阈值后短时间拒绝登录。
 * 成功登录后清理失败计数。
 * </p>
 *
 * @author NYNU Code Lab
 */
public interface LoginAttemptService {

    /**
     * 记录一次登录失败。
     *
     * @param ip      客户端 IP
     * @param username 登录用户名
     */
    void recordFailure(String ip, String username);

    /**
     * 登录成功后清理失败记录。
     *
     * @param ip      客户端 IP
     * @param username 登录用户名
     */
    void clearAttempts(String ip, String username);

    /**
     * 检查当前是否被限流。
     *
     * @param ip      客户端 IP
     * @param username 登录用户名
     * @return true 表示当前被限流，应拒绝登录
     */
    boolean isBlocked(String ip, String username);

    /**
     * 获取剩余锁定秒数（用于提示用户），未锁定时返回 0。
     *
     * @param ip      客户端 IP
     * @param username 登录用户名
     * @return 剩余锁定秒数
     */
    long getRemainingBlockSeconds(String ip, String username);

}
