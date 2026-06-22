package cn.edu.nynu.codelab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 手动配置。
 *
 * <p>Spring Boot 的 {@code RedisAutoConfiguration} 已在 application.yml 中排除，
 * 由本类在 {@code app.auth.redis-enabled=true} 时手动创建连接工厂和 Template。
 * 这样在开发环境未启用 Redis 时，应用不会因 Redis 不可达而启动失败。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Configuration
@ConditionalOnProperty(prefix = "app.auth", name = "redis-enabled", havingValue = "true")
public class RedisConfig {

    @Value("${app.redis.host:localhost}")
    private String host;

    @Value("${app.redis.port:6379}")
    private int port;

    @Value("${app.redis.password:}")
    private String password;

    @Value("${app.redis.database:0}")
    private int database;

    @Value("${app.redis.timeout:3000}")
    private long timeoutMs;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database);
        if (password != null && !password.isEmpty()) {
            config.setPassword(RedisPassword.of(password));
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeoutMs))
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();
        return template;
    }
}
