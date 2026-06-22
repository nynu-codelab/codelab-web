package cn.edu.nynu.codelab.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cache 配置。
 * <p>
 * 默认使用 ConcurrentMapCache（内存实现），无需额外依赖。
 * Redis 环境下可添加 {@code spring.cache.type=redis} 切换。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
