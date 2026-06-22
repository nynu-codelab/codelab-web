package cn.edu.nynu.codelab.site.controller;

import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.site.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 前台站点配置接口（公开访问）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @GetMapping
    public Result<Map<String, String>> configMap() {
        Map<String, String> map = siteConfigService.getConfigMap();
        return Result.success(map);
    }

    @GetMapping("/{key}")
    public Result<String> configValue(@PathVariable String key) {
        String value = siteConfigService.getConfigValue(key);
        return Result.success(value);
    }
}
