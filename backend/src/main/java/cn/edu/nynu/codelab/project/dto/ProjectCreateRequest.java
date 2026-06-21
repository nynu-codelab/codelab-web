package cn.edu.nynu.codelab.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目成果创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class ProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    private String title;

    private String summary;

    private String coverUrl;

    @NotBlank(message = "项目详情不能为空")
    private String descriptionMarkdown;

    private String projectType;

    private String techStack;

    private String leaderName;

    private String membersText;

    private String repoUrl;

    private String demoUrl;

    private String documentUrl;

    private Integer featured;

    private Integer sortOrder;
}
