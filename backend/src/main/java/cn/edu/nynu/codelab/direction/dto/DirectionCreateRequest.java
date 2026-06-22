package cn.edu.nynu.codelab.direction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实验室方向创建/更新请求 DTO
 *
 * @author NYNU Code Lab
 */
@Data
public class DirectionCreateRequest {

    @NotBlank(message = "方向名称不能为空")
    private String name;

    @NotBlank(message = "方向编码不能为空")
    private String code;

    @NotBlank(message = "方向简介不能为空")
    private String summary;

    /** 详细描述（Markdown） */
    private String description;

    /** 标签（JSON 数组字符串，例如 ["前端","React"]） */
    private String tags;

    /** 图标 URL */
    private String icon;

    /** 封面图 URL */
    private String coverUrl;

    /** 排序序号 */
    private Integer sortOrder;
}
