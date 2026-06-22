package cn.edu.nynu.codelab.site.service;

import cn.edu.nynu.codelab.site.entity.SiteConfig;

import java.util.List;
import java.util.Map;

/**
 * 站点配置服务接口
 *
 * @author NYNU Code Lab
 */
public interface SiteConfigService {

    /**
     * 获取所有配置项
     */
    List<SiteConfig> listAll();

    /**
     * 获取所有配置的键值对 Map（configKey → configValue）
     */
    Map<String, String> getConfigMap();

    /**
     * 根据键获取配置值
     */
    String getConfigValue(String key);

    /**
     * 根据键获取配置实体
     */
    SiteConfig getByKey(String key);

    /**
     * 根据键更新配置值
     */
    SiteConfig updateByKey(String key, String value);
}
