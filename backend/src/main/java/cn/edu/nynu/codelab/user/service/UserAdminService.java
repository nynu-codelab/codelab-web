package cn.edu.nynu.codelab.user.service;

import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.user.dto.AdminUserQueryDTO;
import cn.edu.nynu.codelab.user.dto.ResetPasswordDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserRoleDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserStatusDTO;
import cn.edu.nynu.codelab.user.vo.AdminUserVO;

/**
 * 管理员用户管理服务接口
 *
 * @author NYNU Code Lab
 */
public interface UserAdminService {

    /**
     * 分页查询用户列表，支持关键词搜索和角色/状态筛选。
     *
     * @param query 查询参数
     * @return 分页结果（password 字段不返回）
     */
    PageResult<AdminUserVO> listUsers(AdminUserQueryDTO query);

    /**
     * 查看用户详情。
     *
     * @param id 用户 ID
     * @return 用户信息（password 字段不返回）
     */
    AdminUserVO getUserDetail(Long id);

    /**
     * 启用/禁用用户。
     *
     * @param id         目标用户 ID
     * @param dto        状态更新请求
     * @param operatorId 操作者 ID（当前登录管理员）
     */
    void updateUserStatus(Long id, UpdateUserStatusDTO dto, Long operatorId);

    /**
     * 修改用户角色。
     *
     * @param id         目标用户 ID
     * @param dto        角色更新请求
     * @param operatorId 操作者 ID（当前登录管理员）
     */
    void updateUserRole(Long id, UpdateUserRoleDTO dto, Long operatorId);

    /**
     * 重置用户密码（管理员指定新密码）。
     *
     * @param id         目标用户 ID
     * @param dto        新密码
     * @param operatorId 操作者 ID（当前登录管理员）
     * @return 如果重置的是自己，返回提示信息
     */
    String resetPassword(Long id, ResetPasswordDTO dto, Long operatorId);

}
