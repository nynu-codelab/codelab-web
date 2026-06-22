package cn.edu.nynu.codelab.site.service.impl;

import cn.edu.nynu.codelab.site.entity.SiteConfig;
import cn.edu.nynu.codelab.site.mapper.SiteConfigMapper;
import cn.edu.nynu.codelab.site.service.SiteConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 站点配置服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    @Override
    public List<SiteConfig> listAll() {
        return siteConfigMapper.selectList(
                new LambdaQueryWrapper<SiteConfig>()
                        .orderByAsc(SiteConfig::getGroupName)
                        .orderByAsc(SiteConfig::getId)
        );
    }

    @Override
    public Map<String, String> getConfigMap() {
        List<SiteConfig> configs = siteConfigMapper.selectList(null);
        return configs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue, (v1, v2) -> v2));
    }

    @Override
    public String getConfigValue(String key) {
        SiteConfig config = getByKey(key);
        return config.getConfigValue();
    }

    @Override
    public SiteConfig getByKey(String key) {
        SiteConfig config = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfig>()
                        .eq(SiteConfig::getConfigKey, key)
        );
        if (config == null) {
            throw new RuntimeException("配置项不存在: " + key);
        }
        return config;
    }

    @Override
    public SiteConfig updateByKey(String key, String value) {
        SiteConfig config = getByKey(key);
        config.setConfigValue(value);
        siteConfigMapper.updateById(config);
        log.info("站点配置已更新: {} = {}", key, value);
        return config;
    }
}
