package cn.edu.nynu.codelab.auth.controller;

import cn.edu.nynu.codelab.auth.dto.ChangePasswordDTO;
import cn.edu.nynu.codelab.auth.dto.LoginDTO;
import cn.edu.nynu.codelab.auth.dto.RegisterDTO;
import cn.edu.nynu.codelab.auth.service.AuthService;
import cn.edu.nynu.codelab.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 注册（含 IP 级别频率限制，防批量注册）
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        return Result.success(authService.register(dto, ip));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        return Result.success(authService.login(dto, ip));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<?> me() {
        return Result.success(authService.me());
    }

    /**
     * 登出。
     * 服务端使当前 token 失效（加入黑名单）。
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        authService.logout();
        return Result.success("已退出登录");
    }

    /**
     * 修改密码。
     * 成功后当前 token 失效，需重新登录。
     */
    @PostMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return Result.success("密码修改成功，请重新登录");
    }

    /**
     * 获取客户端真实 IP。
     * <p>
     * 仅信任 Nginx 反向代理设置的 X-Forwarded-For / X-Real-IP 头。
     * 对提取的 IP 做基本格式校验（IPv4/IPv6），拒绝明显伪造的值。
     * </p>
     */
    private String getClientIp(HttpServletRequest request) {
        // 优先从 Nginx 设置的 X-Forwarded-For 取第一个 IP
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty() && !"unknown".equalsIgnoreCase(forwarded)) {
            // X-Forwarded-For 可能包含逗号分隔的多个 IP，取最左边的原始客户端 IP
            String firstIp = forwarded.split(",")[0].trim();
            if (isValidIp(firstIp)) {
                return firstIp;
            }
        }

        // 回退到 X-Real-IP（Nginx 单代理时更可靠）
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty() && !"unknown".equalsIgnoreCase(realIp)) {
            if (isValidIp(realIp)) {
                return realIp;
            }
        }

        // 最终回退到直连 IP（本地开发 / 无代理场景）
        return request.getRemoteAddr();
    }

    /** 基本 IP 格式校验——拒绝明显的非法值（内部 IP、hostname、超长字符串） */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.length() > 45) return false; // IPv6 最长 45 字符
        // IPv4: 只包含数字和点
        if (ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) return true;
        // IPv6: 包含冒号
        if (ip.contains(":") && ip.matches("^[0-9a-fA-F:]+$")) return true;
        return false;
    }

}
