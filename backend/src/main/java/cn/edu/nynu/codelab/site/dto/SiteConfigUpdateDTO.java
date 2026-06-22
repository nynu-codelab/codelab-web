package cn.edu.nynu.codelab.site.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 站点配置更新请求
 *
 * @author NYNU Code Lab
 */
@Data
public class SiteConfigUpdateDTO {

    @NotBlank(message = "配置键名不能为空")
    private String configKey;

    @NotBlank(message = "配置值不能为空")
    private String configValue;

    private String configType = "text";

    private String groupName;

    private String remark;
}
