package cn.edu.nynu.codelab.user.dto;

import lombok.Data;

/**
 * 管理员用户列表查询参数
 *
 * @author NYNU Code Lab
 */
@Data
public class AdminUserQueryDTO {

    /** 页码，从 1 开始 */
    private Integer page = 1;

    /** 每页大小 */
    private Integer pageSize = 10;

    /** 搜索关键词（匹配 username、real_name、phone） */
    private String keyword;

    /** 角色筛选：USER / ADMIN */
    private String role;

    /** 状态筛选：1-正常，0-禁用 */
    private Integer status;

}
