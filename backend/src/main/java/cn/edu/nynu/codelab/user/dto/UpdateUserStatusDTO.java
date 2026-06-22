package cn.edu.nynu.codelab.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新用户状态请求
 *
 * @author NYNU Code Lab
 */
@Data
public class UpdateUserStatusDTO {

    /** 状态：1-正常，0-禁用 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值只能为 0 或 1")
    @Max(value = 1, message = "状态值只能为 0 或 1")
    private Integer status;

}
