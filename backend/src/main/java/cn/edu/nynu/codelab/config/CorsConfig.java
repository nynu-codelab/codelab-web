package cn.edu.nynu.codelab.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 跨域配置——通过环境变量 CORS_ALLOWED_ORIGINS 指定允许的来源域名。
 *
 * <p>禁止使用 {@code allowedOriginPattern("*") + allowCredentials(true)} 组合：
 * CORS 规范不允许凭据模式与通配符来源共存，任意网站可利用已登录用户凭证发起跨域请求。</p>
 *
 * <p>配置方式：
 * <pre>{@code
 *   # 本地开发（默认值）
 *   CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174
 *
 *   # 生产部署
 *   CORS_ALLOWED_ORIGINS=https://nynu-code-lab.example.com,https://admin.example.com
 * }</pre>
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Configuration
public class CorsConfig {

    private static final String LOCAL_DEV_ORIGINS = "http://localhost:5173,http://localhost:5174";

    @Value("${CORS_ALLOWED_ORIGINS:" + LOCAL_DEV_ORIGINS + "}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        List<String> origins = parseOrigins(allowedOrigins);

        if (origins.isEmpty()) {
            log.warn("CORS 允许来源列表为空——跨域请求将被拒绝。请在环境变量 CORS_ALLOWED_ORIGINS 中配置域名。");
        } else {
            log.info("CORS 允许来源: {}", origins);
        }

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private List<String> parseOrigins(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

}
