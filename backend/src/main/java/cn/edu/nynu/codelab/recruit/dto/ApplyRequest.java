package cn.edu.nynu.codelab.recruit.dto;

import cn.edu.nynu.codelab.common.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 报名提交/修改请求
 */
@Data
public class ApplyRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @NotBlank(message = "年级不能为空")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业长度不能超过100个字符")
    private String major;

    @NotBlank(message = "班级不能为空")
    @Size(max = 100, message = "班级长度不能超过100个字符")
    private String className;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "QQ号不能为空")
    @Pattern(regexp = "^[1-9]\\d{4,10}$", message = "QQ号格式不正确")
    private String qq;

    @NotBlank(message = "意向技术方向不能为空")
    private String direction;

    private Integer hasProgrammingBasis;

    @Size(max = 500, message = "技能描述长度超限")
    private String skills;

    @Size(max = 2000, message = "个人介绍长度超限")
    private String introduction;

    @Size(max = 2000, message = "申请理由长度超限")
    private String reason;

    @Size(max = 100, message = "每周可投入时间描述长度超限")
    private String weeklyAvailableTime;

    @Size(max = 500, message = "作品链接长度超限")
    @Pattern(regexp = ValidationPatterns.HTTP_URL, message = "作品链接仅支持 http(s)")
    private String portfolioUrl;
}
