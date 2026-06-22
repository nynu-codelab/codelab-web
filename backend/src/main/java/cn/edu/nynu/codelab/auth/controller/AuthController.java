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
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
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
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}
