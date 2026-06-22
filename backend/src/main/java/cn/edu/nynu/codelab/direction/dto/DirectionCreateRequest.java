package cn.edu.nynu.codelab.direction.dto;

import cn.edu.nynu.codelab.common.ValidationPatterns;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实验室方向创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class DirectionCreateRequest {

    @NotBlank(message = "方向名称不能为空")
    @Size(max = 50, message = "方向名称长度不能超过50个字符")
    private String name;

    @NotBlank(message = "方向编码不能为空")
    @Size(max = 50, message = "方向编码长度不能超过50个字符")
    private String code;

    @NotBlank(message = "方向简介不能为空")
    @Size(max = 200, message = "方向简介长度不能超过200个字符")
    private String summary;

    /** 详细描述（Markdown） */
    @Size(max = 500, message = "方向描述长度不能超过500个字符")
    private String description;

    /** 标签（JSON 数组字符串，例如 ["前端","React"]） */
    @Size(max = 300, message = "方向标签长度不能超过300个字符")
    private String tags;

    /** 图标 URL */
    @Size(max = 200, message = "图标字段长度不能超过200个字符")
    private String icon;

    /** 封面图 URL */
    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    @Pattern(regexp = ValidationPatterns.HTTP_OR_RELATIVE_URL, message = "封面图URL仅支持 http(s) 或站内 /uploads 路径")
    private String coverUrl;

    /** 排序序号 */
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能超过9999")
    private Integer sortOrder;
}
