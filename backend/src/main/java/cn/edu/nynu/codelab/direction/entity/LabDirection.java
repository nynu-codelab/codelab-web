package cn.edu.nynu.codelab.direction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验室方向实体
 *
 * @author NYNU Code Lab
 */
@Data
@TableName("lab_direction")
public class LabDirection {

    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 方向名称 */
    private String name;

    /** 方向编码（唯一标识，用于前端路由） */
    private String code;

    /** 简介（一句话描述） */
    private String summary;

    /** 详细描述（Markdown） */
    private String description;

    /** 标签（JSON 数组字符串） */
    private String tags;

    /** 图标 URL */
    private String icon;

    /** 封面图 URL */
    private String coverUrl;

    /** 排序序号（数值越小越靠前） */
    private Integer sortOrder;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
