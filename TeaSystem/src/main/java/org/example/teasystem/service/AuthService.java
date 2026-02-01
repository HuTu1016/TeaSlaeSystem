package org.example.teasystem.service;

import org.example.teasystem.dto.auth.LoginRequest;
import org.example.teasystem.dto.auth.LoginResponse;
import org.example.teasystem.dto.auth.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    Long register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request, String ip);

    /**
     * 退出登录
     */
    void logout(Long userId);

    /**
     * 商家注册
     */
    Long merchantRegister(org.example.teasystem.dto.auth.MerchantRegisterRequest request);

    /**
     * 短信验证码登录
     */
    LoginResponse smsLogin(org.example.teasystem.dto.auth.SmsLoginRequest request, String ip);

    /**
     * 重置密码
     */
    void resetPassword(org.example.teasystem.dto.auth.ResetPasswordRequest request);

    /**
     * 检查用户名是否已存在（按角色）
     */
    boolean checkUsernameExists(String username, String role);

    /**
     * 检查手机号是否已存在（按角色）
     */
    boolean checkPhoneExists(String phone, String role);

    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String refreshToken);
}
