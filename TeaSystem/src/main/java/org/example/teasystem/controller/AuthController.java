package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.auth.*;
import org.example.teasystem.service.AuthService;
import org.example.teasystem.service.SmsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 */
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SmsService smsService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        return Result.success(userId);
    }

    @Operation(summary = "商家注册")
    @PostMapping("/merchant-register")
    public Result<Long> merchantRegister(
            @Valid @RequestBody org.example.teasystem.dto.auth.MerchantRegisterRequest request) {
        Long userId = authService.merchantRegister(request);
        return Result.success(userId);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        LoginResponse response = authService.login(request, ip);
        return Result.success(response);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(Authentication authentication) {
        if (authentication != null) {
            Long userId = (Long) authentication.getPrincipal();
            authService.logout(userId);
        }
        return Result.success();

    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh-token")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return Result.success(response);
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@Valid @RequestBody SendSmsRequest request) {
        boolean success = smsService.sendVerificationCode(request.getPhone(), request.getType());
        if (success) {
            return Result.success();
        } else {
            return Result.fail(50001, "短信发送失败，请稍后重试");
        }
    }

    @Operation(summary = "短信验证码登录")
    @PostMapping("/sms/login")
    public Result<LoginResponse> smsLogin(@Valid @RequestBody SmsLoginRequest request, HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        LoginResponse response = authService.smsLogin(request, ip);
        return Result.success(response);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }

    @Operation(summary = "检查用户名是否已存在")
    @GetMapping("/check-username")
    public Result<java.util.Map<String, Boolean>> checkUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "USER") String role) {
        boolean exists = authService.checkUsernameExists(username, role);
        return Result.success(java.util.Collections.singletonMap("exists", exists));
    }

    @Operation(summary = "检查手机号是否已存在")
    @GetMapping("/check-phone")
    public Result<java.util.Map<String, Boolean>> checkPhone(
            @RequestParam String phone,
            @RequestParam(defaultValue = "USER") String role) {
        boolean exists = authService.checkPhoneExists(phone, role);
        return Result.success(java.util.Collections.singletonMap("exists", exists));
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
