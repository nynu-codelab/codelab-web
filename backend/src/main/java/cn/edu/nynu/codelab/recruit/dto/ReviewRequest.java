package cn.edu.nynu.codelab.recruit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核请求
 */
@Data
public class ReviewRequest {

    @NotBlank(message = "审核状态不能为空")
    private String status;

    private String reviewRemark;
}
