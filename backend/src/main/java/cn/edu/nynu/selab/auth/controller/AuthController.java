package cn.edu.nynu.selab.auth.controller;

import cn.edu.nynu.selab.auth.dto.LoginDTO;
import cn.edu.nynu.selab.auth.dto.RegisterDTO;
import cn.edu.nynu.selab.auth.service.AuthService;
import cn.edu.nynu.selab.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 *
 * @author NYNU SE Lab
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
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<?> me() {
        return Result.success(authService.me());
    }

}
