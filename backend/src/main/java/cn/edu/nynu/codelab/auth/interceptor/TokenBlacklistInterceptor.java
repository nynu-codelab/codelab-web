package cn.edu.nynu.codelab.auth.interceptor;

import cn.edu.nynu.codelab.auth.service.TokenBlacklistService;
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
 * Token 黑名单拦截器。
 * <p>
 * 在 Sa-Token 拦截器之前执行，检查当前请求的 token 是否已在黑名单中。
 * 如果在 Sa-Token JWT 模式下调用了 logout / change-password，
 * token 会被加入黑名单，后续请求在此处被拦截并返回 401。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistInterceptor implements HandlerInterceptor {

    private final TokenBlacklistService tokenBlacklistService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String token = extractToken(request);
        if (token != null && !token.isEmpty() && tokenBlacklistService.isBlacklisted(token)) {
            log.warn("请求使用已注销 token，已拦截: {}", request.getRequestURI());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("code", 401);
            body.put("message", "token已失效，请重新登录");
            body.put("data", null);
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }
        return true;
    }

    /**
     * 从 Authorization 请求头中提取 Bearer token。
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        // 也尝试从请求参数中获取（兼容某些场景）
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }

}
