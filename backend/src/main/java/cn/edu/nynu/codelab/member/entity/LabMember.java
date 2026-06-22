package cn.edu.nynu.codelab.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实验室成员实体
 *
 * @author NYNU Code Lab
 */
@Data
@TableName("lab_member")
public class LabMember {

    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String avatarUrl;

    private String roleTitle;

    private Long directionId;

    private String grade;

    private String bio;

    private String skills;

    private String githubUrl;

    private String blogUrl;

    private String email;

    private Integer sortOrder;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
