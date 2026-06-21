package cn.edu.nynu.codelab.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @NotBlank(message = "专业不能为空")
    private String major;

    @NotBlank(message = "班级不能为空")
    private String className;

}
