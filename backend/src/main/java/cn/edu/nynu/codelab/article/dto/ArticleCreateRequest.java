package cn.edu.nynu.codelab.article.dto;

import cn.edu.nynu.codelab.common.ValidationPatterns;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文章创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class ArticleCreateRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200个字符")
    private String title;

    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;

    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "封面图URL仅支持 http(s) 或站内 /uploads 路径")
    private String coverUrl;

    @NotBlank(message = "文章内容不能为空")
    @Size(max = 100000, message = "文章内容长度不能超过100000个字符")
    private String contentMarkdown;

    @Size(max = 50, message = "文章分类长度不能超过50个字符")
    private String category;

    @Size(max = 500, message = "文章标签长度不能超过500个字符")
    private String tags;

    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sortOrder;
}
