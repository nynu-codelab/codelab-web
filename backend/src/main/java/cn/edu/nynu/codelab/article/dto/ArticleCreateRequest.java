package cn.edu.nynu.codelab.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文章创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class ArticleCreateRequest {

    @NotBlank(message = "文章标题不能为空")
    private String title;

    private String summary;

    private String coverUrl;

    @NotBlank(message = "文章内容不能为空")
    private String contentMarkdown;

    private String category;

    private String tags;

    private Integer sortOrder;
}
