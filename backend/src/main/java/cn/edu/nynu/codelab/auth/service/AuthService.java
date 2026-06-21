package cn.edu.nynu.codelab.auth.service;

import cn.edu.nynu.codelab.auth.dto.LoginDTO;
import cn.edu.nynu.codelab.auth.dto.RegisterDTO;
import cn.edu.nynu.codelab.user.entity.User;

import java.util.Map;

/**
 * 认证服务接口
 *
 * @author NYNU Code Lab
 */
public interface AuthService {

    /**
     * 注册
     */
    User register(RegisterDTO dto);

    /**
     * 登录，返回 token 和用户信息
     */
    Map<String, Object> login(LoginDTO dto);

    /**
     * 获取当前登录用户信息
     */
    User me();

}
