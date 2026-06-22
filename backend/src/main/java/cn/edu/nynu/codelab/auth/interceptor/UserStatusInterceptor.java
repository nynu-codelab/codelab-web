package cn.edu.nynu.codelab.auth.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.common.exception.AccountDisabledException;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户状态拦截器。
 * <p>
 * 在 Sa-Token 鉴权之后执行（order=3），检查当前登录用户的账号状态。
 * 如果账号已被管理员禁用，直接返回 403，阻止继续访问。
 * </p>
 * <p>
 * 该拦截器覆盖所有 /api/** 鉴权路径，与 {@link TokenBlacklistInterceptor}（order=1）
 * 和 SaInterceptor（order=2）形成三层请求过滤链：
 * <ol>
 *   <li>TokenBlacklistInterceptor — 检查 token 是否已登出/失效</li>
 *   <li>SaInterceptor — 验证 JWT 合法性、登录状态、角色权限</li>
 *   <li>UserStatusInterceptor — 检查账号是否被禁用</li>
 * </ol>
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserStatusInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 仅在用户已登录时检查
        if (!StpUtil.isLogin()) {
            return true;
        }

        try {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userMapper.selectById(userId);
            if (user != null && user.getStatus() == 0) {
                log.warn("已禁用用户尝试访问: userId={}, uri={}", userId, request.getRequestURI());
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("code", 403);
                body.put("message", "账号已被禁用，请联系管理员");
                body.put("data", null);
                response.getWriter().write(objectMapper.writeValueAsString(body));
                return false;
            }
        } catch (AccountDisabledException e) {
            throw e;
        } catch (Exception e) {
            // 如果获取登录 ID 失败（理论上不会发生，因为已经 isLogin()），放行
            log.debug("UserStatusInterceptor: 无法获取用户信息，放行: {}", e.getMessage());
        }

        return true;
    }

}
