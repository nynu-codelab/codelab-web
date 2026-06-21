package cn.edu.nynu.codelab.recruit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 报名提交/修改请求
 */
@Data
public class ApplyRequest {

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @NotBlank(message = "专业不能为空")
    private String major;

    @NotBlank(message = "班级不能为空")
    private String className;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "QQ号不能为空")
    private String qq;

    @NotBlank(message = "意向技术方向不能为空")
    private String direction;

    private Integer hasProgrammingBasis;

    private String skills;

    private String introduction;

    private String reason;

    private String weeklyAvailableTime;

    private String portfolioUrl;
}
