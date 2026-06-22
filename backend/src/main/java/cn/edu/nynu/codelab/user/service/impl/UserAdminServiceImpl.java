package cn.edu.nynu.codelab.user.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.auth.service.TokenBlacklistService;
import cn.edu.nynu.codelab.common.PageResult;
import cn.edu.nynu.codelab.user.dto.AdminUserQueryDTO;
import cn.edu.nynu.codelab.user.dto.ResetPasswordDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserRoleDTO;
import cn.edu.nynu.codelab.user.dto.UpdateUserStatusDTO;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import cn.edu.nynu.codelab.user.service.UserAdminService;
import cn.edu.nynu.codelab.user.vo.AdminUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户管理服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public PageResult<AdminUserVO> listUsers(AdminUserQueryDTO query) {
        int pageNum = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int pageSize = query.getPageSize() != null && query.getPageSize() > 0 ? query.getPageSize() : 10;

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索：username、real_name、phone
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }

        // 角色筛选
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq(User::getRole, query.getRole().trim());
        }

        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq(User::getStatus, query.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreateTime);

        // 先查总数（selectPage 需要分页插件，此处手动 count 避免依赖插件）
        long total = userMapper.selectCount(wrapper);

        // 分页查询记录
        Page<User> page = new Page<>(pageNum, pageSize);
        // 不依赖分页插件，仅用 Page 做 LIMIT/OFFSET
        page.setSearchCount(false);
        Page<User> result = userMapper.selectPage(page, wrapper);

        List<AdminUserVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(records, total, pageNum, pageSize);
    }

    @Override
    public AdminUserVO getUserDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toVO(user);
    }

    @Override
    public void updateUserStatus(Long id, UpdateUserStatusDTO dto, Long operatorId) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能禁用自己
        if (id.equals(operatorId)) {
            throw new RuntimeException("不能禁用自己");
        }

        // 如果要禁用管理员，检查是否是最后一个管理员
        if (dto.getStatus() == 0 && "ADMIN".equals(user.getRole())) {
            long adminCount = countActiveAdmins();
            if (adminCount <= 1) {
                throw new RuntimeException("不能禁用最后一个管理员");
            }
        }

        user.setStatus(dto.getStatus());
        userMapper.updateById(user);

        log.info("管理员 {} 将用户 {} (id={}) 状态更新为 {}",
                operatorId, user.getUsername(), id, dto.getStatus());
    }

    @Override
    public void updateUserRole(Long id, UpdateUserRoleDTO dto, Long operatorId) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不能修改自己的角色
        if (id.equals(operatorId)) {
            throw new RuntimeException("不能修改自己的角色");
        }

        // 如果角色没变，直接返回
        if (dto.getRole().equals(user.getRole())) {
            return;
        }

        // 如果要把管理员降权为普通用户，检查是否是最后一个管理员
        if ("USER".equals(dto.getRole()) && "ADMIN".equals(user.getRole())) {
            long adminCount = countActiveAdmins();
            if (adminCount <= 1) {
                throw new RuntimeException("不能把最后一个管理员降权");
            }
        }

        String oldRole = user.getRole();
        user.setRole(dto.getRole());
        userMapper.updateById(user);

        log.info("管理员 {} 将用户 {} (id={}) 角色从 {} 改为 {}",
                operatorId, user.getUsername(), id, oldRole, dto.getRole());
    }

    @Override
    public String resetPassword(Long id, ResetPasswordDTO dto, Long operatorId) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // BCrypt 加密新密码
        user.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        userMapper.updateById(user);

        log.info("管理员 {} 重置了用户 {} (id={}) 的密码", operatorId, user.getUsername(), id);

        // 如果重置的是自己的密码，当前 token 需要失效
        if (id.equals(operatorId)) {
            String token = StpUtil.getTokenValue();
            if (token != null && !token.isEmpty()) {
                tokenBlacklistService.blacklist(token, tokenBlacklistService.getBlacklistTtlMs());
            }
            StpUtil.logout();
            return "密码已重置，请使用新密码重新登录";
        }

        return "密码重置成功";
    }

    // ---- 私有辅助方法 ----

    /**
     * 将 User 实体转换为 AdminUserVO，确保不泄露密码。
     */
    private AdminUserVO toVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setClassName(user.getClassName());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        // 刻意不设置 password 字段
        return vo;
    }

    /**
     * 统计当前启用的管理员数量（status=1, role=ADMIN）。
     */
    private long countActiveAdmins() {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "ADMIN")
                .eq(User::getStatus, 1));
    }

}
