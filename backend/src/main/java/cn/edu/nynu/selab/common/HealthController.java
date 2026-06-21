package cn.edu.nynu.selab.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查接口 — 供 Docker Compose healthcheck 和运维监控使用
 *
 * @author NYNU SE Lab
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("service", "nynu-se-lab-backend");
        status.put("timestamp", LocalDateTime.now().toString());
        return Result.success(status);
    }
}
