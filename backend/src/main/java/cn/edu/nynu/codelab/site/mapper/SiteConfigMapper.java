package cn.edu.nynu.codelab.site.mapper;

import cn.edu.nynu.codelab.site.entity.SiteConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站点配置 Mapper
 *
 * @author NYNU Code Lab
 */
@Mapper
public interface SiteConfigMapper extends BaseMapper<SiteConfig> {
}
