package org.example.teasystem.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 */
@Data
public class LoginRequest {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户角色（USER/MERCHANT/ADMIN），不同端登录时传对应角色
     * 用户端默认 USER，商家端传 MERCHANT
     */
    private String role = "USER";
}
