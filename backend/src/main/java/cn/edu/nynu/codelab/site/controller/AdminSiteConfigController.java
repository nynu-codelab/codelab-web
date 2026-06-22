package cn.edu.nynu.codelab.site.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.site.dto.SiteConfigUpdateDTO;
import cn.edu.nynu.codelab.site.entity.SiteConfig;
import cn.edu.nynu.codelab.site.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台站点配置管理接口（需 ADMIN 权限）
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/site-config")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminSiteConfigController {

    private final SiteConfigService siteConfigService;

    @GetMapping
    public Result<List<SiteConfig>> list() {
        List<SiteConfig> configs = siteConfigService.listAll();
        return Result.success(configs);
    }

    @PutMapping("/{key}")
    public Result<SiteConfig> update(@PathVariable String key, @RequestBody SiteConfigUpdateDTO dto) {
        SiteConfig config = siteConfigService.updateByKey(key, dto.getConfigValue());
        return Result.success(config);
    }
}
