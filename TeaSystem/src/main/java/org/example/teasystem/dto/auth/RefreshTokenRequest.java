package org.example.teasystem.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 刷新令牌请求
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}
