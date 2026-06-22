package cn.edu.nynu.codelab.project.dto;

import cn.edu.nynu.codelab.common.ValidationPatterns;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 项目成果创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class ProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    private String title;

    @Size(max = 500, message = "项目摘要长度不能超过500个字符")
    private String summary;

    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "封面图URL仅支持 http(s) 或站内 /uploads 路径")
    private String coverUrl;

    @NotBlank(message = "项目详情不能为空")
    @Size(max = 100000, message = "项目详情长度不能超过100000个字符")
    private String descriptionMarkdown;

    @Size(max = 50, message = "项目类型长度不能超过50个字符")
    private String projectType;

    @Size(max = 500, message = "技术栈长度不能超过500个字符")
    private String techStack;

    @Size(max = 50, message = "负责人姓名长度不能超过50个字符")
    private String leaderName;

    @Size(max = 500, message = "成员信息长度不能超过500个字符")
    private String membersText;

    @Size(max = 500, message = "代码仓库链接长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "代码仓库链接仅支持 http(s) 或站内路径")
    private String repoUrl;

    @Size(max = 500, message = "演示链接长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "演示链接仅支持 http(s) 或站内路径")
    private String demoUrl;

    @Size(max = 500, message = "文档链接长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "文档链接仅支持 http(s) 或站内路径")
    private String documentUrl;

    @Min(value = 0, message = "精选状态只能为0或1")
    @Max(value = 1, message = "精选状态只能为0或1")
    private Integer featured;

    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sortOrder;
}
