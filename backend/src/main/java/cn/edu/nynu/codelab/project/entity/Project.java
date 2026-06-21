package cn.edu.nynu.codelab.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目成果实体
 *
 * @author NYNU Code Lab
 */
@Data
@TableName("lab_project")
public class Project {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_OFFLINE = "OFFLINE";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String descriptionMarkdown;

    private String projectType;

    private String techStack;

    private String leaderName;

    private String membersText;

    private String repoUrl;

    private String demoUrl;

    private String documentUrl;

    private String status;

    private Integer featured;

    private Integer viewCount;

    private Integer sortOrder;

    private Long authorId;

    private LocalDateTime publishedAt;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
