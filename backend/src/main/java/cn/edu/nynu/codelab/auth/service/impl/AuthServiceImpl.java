package cn.edu.nynu.codelab.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.codelab.auth.dto.ChangePasswordDTO;
import cn.edu.nynu.codelab.auth.dto.LoginDTO;
import cn.edu.nynu.codelab.auth.dto.RegisterDTO;
import cn.edu.nynu.codelab.auth.service.AuthService;
import cn.edu.nynu.codelab.auth.service.LoginAttemptService;
import cn.edu.nynu.codelab.auth.service.TokenBlacklistService;
import cn.edu.nynu.codelab.common.exception.AccountDisabledException;
import cn.edu.nynu.codelab.user.entity.User;
import cn.edu.nynu.codelab.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现
 *
 * @author NYNU Code Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginAttemptService loginAttemptService;

    @Override
    public User register(RegisterDTO dto) {
        // 检查密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名唯一性
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (usernameCount > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号唯一性
        Long phoneCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (phoneCount > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 构建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setGrade(dto.getGrade());
        user.setMajor(dto.getMajor());
        user.setClassName(dto.getClassName());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);

        // 清除密码后返回
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<String, Object> login(LoginDTO dto, String ip) {
        // 检查是否被限流
        if (loginAttemptService.isBlocked(ip, dto.getUsername())) {
            long remaining = loginAttemptService.getRemainingBlockSeconds(ip, dto.getUsername());
            throw new RuntimeException("登录尝试过于频繁，请 " + remaining + " 秒后重试");
        }

        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            loginAttemptService.recordFailure(ip, dto.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(ip, dto.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查是否禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 登录成功，清理失败计数
        loginAttemptService.clearAttempts(ip, dto.getUsername());

        // Sa-Token 登录
        StpUtil.login(user.getId());

        // 返回 token 和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        user.setPassword(null);
        result.put("user", user);
        return result;
    }

    @Override
    public User me() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 检查账号是否已被禁用
        if (user.getStatus() == 0) {
            throw new AccountDisabledException();
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void logout() {
        String token = StpUtil.getTokenValue();
        if (token != null && !token.isEmpty()) {
            // 将当前 token 加入黑名单
            tokenBlacklistService.blacklist(token, tokenBlacklistService.getBlacklistTtlMs());
            log.info("用户 {} 登出，token 已加入黑名单", StpUtil.getLoginId());
        }
        // Sa-Token 登出（清除会话）
        StpUtil.logout();
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        // 校验新密码与确认密码一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }

        // 校验新密码长度
        if (dto.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }

        // 获取当前用户
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 校验旧密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 新密码不能与旧密码相同
        if (BCrypt.checkpw(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(dto.getNewPassword()));
        userMapper.updateById(user);

        // 将当前 token 加入黑名单，强制用户重新登录
        String token = StpUtil.getTokenValue();
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.blacklist(token, tokenBlacklistService.getBlacklistTtlMs());
        }
        StpUtil.logout();

        log.info("用户 {} 修改密码成功，token 已失效", userId);
    }

}
