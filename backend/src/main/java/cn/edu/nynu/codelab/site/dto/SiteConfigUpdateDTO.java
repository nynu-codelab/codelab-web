package cn.edu.nynu.codelab.site.dto;

import lombok.Data;

/**
 * 站点配置更新请求
 *
 * @author NYNU Code Lab
 */
@Data
public class SiteConfigUpdateDTO {

    private String configKey;

    private String configValue;

    private String configType = "text";

    private String groupName;

    private String remark;
}
