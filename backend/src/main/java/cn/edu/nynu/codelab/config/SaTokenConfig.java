package cn.edu.nynu.codelab.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
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
 * Sa-Token 配置
 *
 * @author NYNU Code Lab
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer, StpInterface {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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
                );
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
