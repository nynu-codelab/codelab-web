package cn.edu.nynu.codelab.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查接口 — 供 Docker Compose healthcheck 和运维监控使用。
 * <p>
 * 检查 DB 和 Redis（可选，本地开发可能未启用）连通性，
 * Docker Compose 使用 {@code curl -f} 检查 HTTP 状态码（2xx 通过）。
 * </p>
 *
 * @author NYNU Code Lab
 */
@Slf4j
@RestController
public class HealthController {

    private final UserMapper userMapper;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    public HealthController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Result<Map<String, Object>>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("service", "nynu-code-lab-backend");
        status.put("timestamp", LocalDateTime.now().toString());

        // 数据库连通性检查
        boolean dbOk = checkDb();
        status.put("database", dbOk ? "UP" : "DOWN");

        // Redis 连通性检查
        boolean redisOk = checkRedis();
        status.put("redis", stringRedisTemplate == null ? "DISABLED" : (redisOk ? "UP" : "DOWN"));

        if (dbOk && (redisOk || stringRedisTemplate == null)) {
            status.put("status", "UP");
            return ResponseEntity.ok(Result.success(status));
        }

        status.put("status", "DEGRADED");
        log.warn("健康检查降级: db={}, redis={}", dbOk ? "UP" : "DOWN", redisOk ? "UP" : "DOWN");
        // 返回 503 使 Docker healthcheck curl -f 失败，触发容器重启
        return ResponseEntity.status(503).body(new Result<>(503, "Service degraded", status));
    }

    private boolean checkDb() {
        try {
            userMapper.selectCount(new LambdaQueryWrapper<User>().last("LIMIT 1"));
            return true;
        } catch (Exception e) {
            log.error("数据库健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkRedis() {
        if (stringRedisTemplate == null) {
            return false;
        }
        try {
            String pong = stringRedisTemplate.getConnectionFactory().getConnection().ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            log.error("Redis 健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}
