package cn.edu.nynu.codelab.auth.service;

import cn.edu.nynu.codelab.auth.dto.ChangePasswordDTO;
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
     *
     * @param dto 注册请求
     * @param ip  客户端 IP（用于注册限流）
     */
    User register(RegisterDTO dto, String ip);

    /**
     * 登录，返回 token 和用户信息
     */
    Map<String, Object> login(LoginDTO dto, String ip);

    /**
     * 获取当前登录用户信息
     */
    User me();

    /**
     * 登出，使当前 token 失效
     */
    void logout();

    /**
     * 修改密码，成功后当前 token 失效需重新登录
     */
    void changePassword(ChangePasswordDTO dto);

}
