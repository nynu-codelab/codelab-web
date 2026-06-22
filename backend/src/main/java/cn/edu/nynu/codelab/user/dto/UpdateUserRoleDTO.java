package cn.edu.nynu.codelab.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新用户角色请求
 *
 * @author NYNU Code Lab
 */
@Data
public class UpdateUserRoleDTO {

    /** 角色：USER 或 ADMIN */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "USER|ADMIN", message = "角色只能为 USER 或 ADMIN")
    private String role;

}
