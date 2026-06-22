package cn.edu.nynu.codelab.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * JWT 密钥启动校验器——在应用启动时验证 JWT 密钥已正确配置。
 *
 * <p>如果环境变量 JWT_SECRET 未设置或过短（&lt; 64 字符），应用将在启动阶段终止，
 * 而非携带弱密钥上线运行。</p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Component
public class JwtSecretStartupValidator implements ApplicationRunner {

    @Value("${sa-token.jwt-secret-key}")
    private String jwtSecretKey;

    private static final int MIN_KEY_LENGTH = 64;

    @Override
    public void run(ApplicationArguments args) {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            log.error("============================================================");
            log.error("  [FATAL] JWT_SECRET 环境变量未设置！");
            log.error("  应用拒绝启动。请在环境中设置 JWT_SECRET（≥ {} 字符）。", MIN_KEY_LENGTH);
            log.error("  示例: export JWT_SECRET=\"$(openssl rand -base64 64)\"");
            log.error("============================================================");
            throw new IllegalStateException(
                    "JWT_SECRET 环境变量未设置。请设置至少 " + MIN_KEY_LENGTH + " 字符的密钥后重试。"
            );
        }

        if (jwtSecretKey.length() < MIN_KEY_LENGTH) {
            log.error("============================================================");
            log.error("  [FATAL] JWT_SECRET 长度不足！");
            log.error("  当前长度: {} 字符，要求至少: {} 字符。", jwtSecretKey.length(), MIN_KEY_LENGTH);
            log.error("  请使用更强的密钥: openssl rand -base64 64");
            log.error("============================================================");
            throw new IllegalStateException(
                    "JWT_SECRET 密钥长度不足（当前 " + jwtSecretKey.length()
                            + " 字符，要求至少 " + MIN_KEY_LENGTH + " 字符）。"
            );
        }

        log.info("JWT 密钥校验通过（长度: {} 字符）", jwtSecretKey.length());
    }
}
