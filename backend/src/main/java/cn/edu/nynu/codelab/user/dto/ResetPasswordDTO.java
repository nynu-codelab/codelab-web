package cn.edu.nynu.codelab.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员重置用户密码请求
 *
 * @author NYNU Code Lab
 */
@Data
public class ResetPasswordDTO {

    /** 新密码（管理员指定） */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度不能少于6位")
    private String newPassword;

}
