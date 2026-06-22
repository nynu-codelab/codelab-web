package cn.edu.nynu.codelab.user.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.common.Result;
import cn.edu.nynu.codelab.user.dto.AdminUserQueryDTO;
import cn.edu.nynu.codelab.user.dto.ResetPasswordDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserRoleDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserStatusDTO;
import cn.edu.nynu.codelab.user.service.UserAdminService;
import cn.edu.nynu.codelab.user.vo.AdminUserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户管理接口
 *
 * @author NYNU Code Lab
 */
@RestController
@RequestMapping("/api/admin/users")
@SaCheckRole("ADMIN")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * 分页查询用户列表。
     */
    @GetMapping
    public Result<PageResult<AdminUserVO>> listUsers(AdminUserQueryDTO query) {
        PageResult<AdminUserVO> page = userAdminService.listUsers(query);
        return Result.success(page);
    }

    /**
     * 查看用户详情。
     */
    @GetMapping("/{id}")
    public Result<AdminUserVO> getUserDetail(@PathVariable Long id) {
        AdminUserVO user = userAdminService.getUserDetail(id);
        return Result.success(user);
    }

    /**
     * 启用/禁用用户。
     */
    @PatchMapping("/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id,
                                      @Valid @RequestBody UpdateUserStatusDTO dto) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        userAdminService.updateUserStatus(id, dto, operatorId);
        String action = dto.getStatus() == 1 ? "启用" : "禁用";
        return Result.success("用户已" + action);
    }

    /**
     * 修改用户角色。
     */
    @PatchMapping("/{id}/role")
    public Result<?> updateUserRole(@PathVariable Long id,
                                    @Valid @RequestBody UpdateUserRoleDTO dto) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        userAdminService.updateUserRole(id, dto, operatorId);
        return Result.success("角色修改成功");
    }

    /**
     * 重置用户密码（管理员指定新密码）。
     */
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id,
                                   @Valid @RequestBody ResetPasswordDTO dto) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        String message = userAdminService.resetPassword(id, dto, operatorId);
        return Result.success(message);
    }

}
