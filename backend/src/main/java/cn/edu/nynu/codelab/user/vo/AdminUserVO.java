package cn.edu.nynu.codelab.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员视角的用户信息（不含密码和逻辑删除标志）。
 *
 * @author NYNU Code Lab
 */
@Data
public class AdminUserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String grade;
    private String major;
    private String className;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
