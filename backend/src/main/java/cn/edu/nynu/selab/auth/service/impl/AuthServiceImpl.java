package cn.edu.nynu.selab.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.edu.nynu.selab.auth.dto.LoginDTO;
import cn.edu.nynu.selab.auth.dto.RegisterDTO;
import cn.edu.nynu.selab.auth.service.AuthService;
import cn.edu.nynu.selab.user.entity.User;
import cn.edu.nynu.selab.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现
 *
 * @author NYNU SE Lab
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

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
    public Map<String, Object> login(LoginDTO dto) {
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查是否禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

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
        user.setPassword(null);
        return user;
    }

}
