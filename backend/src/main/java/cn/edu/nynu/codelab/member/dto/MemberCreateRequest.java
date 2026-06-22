package cn.edu.nynu.codelab.member.dto;

import cn.edu.nynu.codelab.common.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实验室成员创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class MemberCreateRequest {

    @NotBlank(message = "成员姓名不能为空")
    @Size(max = 50, message = "成员姓名长度不能超过50个字符")
    private String name;

    @NotBlank(message = "角色/职位不能为空")
    @Size(max = 100, message = "角色/职位长度不能超过100个字符")
    private String roleTitle;

    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "头像URL仅支持 http(s) 或站内 /uploads 路径")
    private String avatarUrl;

    private Long directionId;

    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;

    @Size(max = 1000, message = "个人简介长度不能超过1000个字符")
    private String bio;

    @Size(max = 500, message = "技能标签长度不能超过500个字符")
    private String skills;

    @Size(max = 300, message = "GitHub 链接长度不能超过300个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "GitHub 链接仅支持 http(s)")
    private String githubUrl;

    @Size(max = 300, message = "博客链接长度不能超过300个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "博客链接仅支持 http(s)")
    private String blogUrl;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sortOrder;

    @Min(value = 0, message = "状态只能为0或1")
    @Max(value = 1, message = "状态只能为0或1")
    private Integer status;
}
