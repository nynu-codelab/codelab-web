package cn.edu.nynu.codelab.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实验室成员创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class MemberCreateRequest {

    @NotBlank(message = "成员姓名不能为空")
    private String name;

    @NotBlank(message = "角色/职位不能为空")
    private String roleTitle;

    private String avatarUrl;

    private Long directionId;

    private String grade;

    private String bio;

    private String skills;

    private String githubUrl;

    private String blogUrl;

    private String email;

    private Integer sortOrder;

    private Integer status;
}
