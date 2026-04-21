package com.jparlant.service;

import com.jparlant.dto.LoginResultDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token和用户信息）
     */
    LoginResultDTO login(String username, String password);

    /**
     * 验证token是否有效
     * @param token token
     * @return 用户ID，无效返回null
     */
    Long validateToken(String token);

    /**
     * 登出
     * @param token token
     */
    void logout(String token);
}
