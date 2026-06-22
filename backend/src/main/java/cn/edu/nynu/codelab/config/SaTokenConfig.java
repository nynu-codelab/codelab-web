package cn.edu.nynu.codelab.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.edu.nynu.codelab.auth.interceptor.TokenBlacklistInterceptor;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 配置 + Token 黑名单拦截器注册
 *
 * @author NYNU Code Lab
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer, StpInterface {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenBlacklistInterceptor tokenBlacklistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Token 黑名单拦截器 — 在 Sa-Token 之前执行，拦截已登出的 token
        registry.addInterceptor(tokenBlacklistInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/health",
                        // 公开阅读接口不需要检查黑名单（不带 token 的请求）
                        "/api/articles/**",
                        "/api/projects/**",
                        "/api/portal/**",
                        "/doc.html",
                        "/v3/**",
                        "/swagger-ui/**"
                )
                .order(1);

        // 2. Sa-Token 鉴权拦截器 — 校验登录态和角色
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/health",
                        "/api/articles/**",
                        "/api/projects/**",
                        "/api/portal/**",
                        "/doc.html",
                        "/v3/**",
                        "/swagger-ui/**"
                )
                .order(2);
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(user.getRole());
    }

}
