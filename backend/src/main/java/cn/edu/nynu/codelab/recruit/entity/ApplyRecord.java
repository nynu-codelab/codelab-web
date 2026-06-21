package cn.edu.nynu.codelab.recruit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招新报名记录实体
 */
@Data
@TableName("lab_apply_record")
public class ApplyRecord {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PRELIMINARY_PASSED = "PRELIMINARY_PASSED";
    public static final String STATUS_INTERVIEWING = "INTERVIEWING";
    public static final String STATUS_PASSED = "PASSED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_WITHDRAWN = "WITHDRAWN";

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    private String grade;

    private String major;

    private String className;

    private String phone;

    private String qq;

    private String direction;

    private Integer hasProgrammingBasis;

    private String skills;

    private String introduction;

    private String reason;

    private String weeklyAvailableTime;

    private String portfolioUrl;

    private String status;

    private String reviewRemark;

    private Long reviewerId;

    private LocalDateTime reviewedAt;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
