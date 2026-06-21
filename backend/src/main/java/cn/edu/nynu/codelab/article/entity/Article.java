package cn.edu.nynu.codelab.article.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章实体
 *
 * @author NYNU Code Lab
 */
@Data
@TableName("lab_article")
public class Article {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_OFFLINE = "OFFLINE";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String contentMarkdown;

    private String category;

    private String tags;

    private String status;

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
