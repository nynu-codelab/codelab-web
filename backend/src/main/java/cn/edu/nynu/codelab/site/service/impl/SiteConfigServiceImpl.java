package cn.edu.nynu.codelab.site.service.impl;

import cn.edu.nynu.codelab.common.exception.BusinessException;
import cn.edu.nynu.codelab.common.ValidationPatterns;
import cn.edu.nynu.codelab.site.entity.SiteConfig;
import cn.edu.nynu.codelab.site.mapper.SiteConfigMapper;
import cn.edu.nynu.codelab.site.service.SiteConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 站点配置服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SiteConfigServiceImpl implements SiteConfigService {

    private static final Set<String> URL_CONFIG_KEYS = Set.of(
            "contactQrcodeUrl",
            "githubUrl"
    );

    private static final Pattern SAFE_URL_PATTERN = Pattern.compile(ValidationPatterns.HTTP_OR_RELATIVE_URL);

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
    @Cacheable(value = "siteConfig", key = "'all'")
    public Map<String, String> getConfigMap() {
        List<SiteConfig> configs = siteConfigMapper.selectList(null);
        return configs.stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, SiteConfig::getConfigValue, (v1, v2) -> v2));
    }

    @Override
    @Cacheable(value = "siteConfig", key = "#key")
    public String getConfigValue(String key) {
        SiteConfig config = getByKey(key);
        return config.getConfigValue();
    }

    @Override
    @Cacheable(value = "siteConfig", key = "#key")
    public SiteConfig getByKey(String key) {
        SiteConfig config = siteConfigMapper.selectOne(
                new LambdaQueryWrapper<SiteConfig>()
                        .eq(SiteConfig::getConfigKey, key)
        );
        if (config == null) {
            throw new BusinessException("配置项不存在: " + key);
        }
        return config;
    }

    @Override
    @CacheEvict(value = "siteConfig", allEntries = true)
    public SiteConfig updateByKey(String key, String value) {
        SiteConfig config = getByKey(key);
        validateConfigValue(config, value);
        config.setConfigValue(value);
        siteConfigMapper.updateById(config);
        log.info("站点配置已更新: {}", key);
        return config;
    }

    private void validateConfigValue(SiteConfig config, String value) {
        boolean urlLikeConfig = URL_CONFIG_KEYS.contains(config.getConfigKey())
                || "image".equalsIgnoreCase(config.getConfigType());
        if (!urlLikeConfig || value == null || value.isBlank()) {
            return;
        }
        if (!SAFE_URL_PATTERN.matcher(value).matches()) {
            throw new BusinessException("配置项 " + config.getConfigKey() + " 仅支持 http(s) 或站内 /uploads 路径");
        }
    }
}
