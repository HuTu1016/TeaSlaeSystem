package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.common.constant.RedisConstants;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.ResultCode;
import org.example.teasystem.common.util.PasswordUtil;
import org.example.teasystem.dto.auth.LoginRequest;
import org.example.teasystem.dto.auth.LoginResponse;
import org.example.teasystem.dto.auth.RegisterRequest;
import org.example.teasystem.entity.User;
import org.example.teasystem.mapper.UserMapper;
import org.example.teasystem.security.JwtTokenProvider;
import org.example.teasystem.service.AuthService;
import org.example.teasystem.service.SmsService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final org.example.teasystem.mapper.MerchantMapper merchantMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final SmsService smsService;

    @Override
    @Transactional
    public Long register(RegisterRequest request) {
        // 验证短信验证码
        if (!smsService.verifyCode(request.getPhone(), request.getCode(), "REGISTER")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误或已过期");
        }

        // 检查密码强度
        if (!PasswordUtil.checkPasswordStrength(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码必须包含字母和数字，长度8-20位");
        }

        // 检查手机号是否已存在（仅检查 USER 角色）
        if (userMapper.findByPhoneAndRole(request.getPhone(), "USER") != null) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 检查用户名是否已存在（仅检查 USER 角色）
        if (request.getUsername() != null && userMapper.findByUsernameAndRole(request.getUsername(), "USER") != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setPasswordHash(PasswordUtil.md5(request.getPassword()));
        user.setNickname(
                request.getUsername() != null ? request.getUsername() : "用户" + request.getPhone().substring(7));
        user.setAvatar("/default-avatar.png"); // 设置默认头像
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功: userId={}, phone={}", user.getId(), PasswordUtil.maskPhone(request.getPhone()));

        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request, String ip) {
        String account = request.getAccount();
        String role = request.getRole() != null ? request.getRole() : "USER";

        // 检查账号是否被锁定
        String lockKey = RedisConstants.ACCOUNT_LOCK_PREFIX + account + ":" + role;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
        }

        // 查询用户（按角色）
        User user = userMapper.findByAccountAndRole(account, role);

        // 验证密码
        if (user == null || !PasswordUtil.verify(request.getPassword(), user.getPasswordHash())) {
            // 记录失败次数
            handleLoginFail(account + ":" + role);
            throw new BusinessException(ResultCode.UNAUTHORIZED, "账号或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 清除失败次数
        String failKey = RedisConstants.LOGIN_FAIL_PREFIX + account + ":" + role;
        redisTemplate.delete(failKey);

        // 更新最后登录信息
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now(), ip);

        // 生成Token
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getRole());

        log.info("用户登录成功: userId={}, role={}, ip={}", user.getId(), user.getRole(), ip);

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .role(user.getRole())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phone(PasswordUtil.maskPhone(user.getPhone()))
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    @Override
    public void logout(Long userId) {
        // MVP阶段仅记录日志，实际可以将Token加入黑名单
        log.info("用户登出: userId={}", userId);
    }

    @Override
    @Transactional
    public Long merchantRegister(org.example.teasystem.dto.auth.MerchantRegisterRequest request) {
        // 检查密码强度
        if (!PasswordUtil.checkPasswordStrength(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码必须包含字母和数字，长度8-20位");
        }

        // 检查手机号是否已存在（仅检查 MERCHANT 角色）
        if (userMapper.findByPhoneAndRole(request.getPhone(), "MERCHANT") != null) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 检查用户名是否已存在（仅检查 MERCHANT 角色）
        if (request.getUsername() != null
                && userMapper.findByUsernameAndRole(request.getUsername(), "MERCHANT") != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 创建用户（角色为MERCHANT）
        User user = new User();
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setPasswordHash(PasswordUtil.md5(request.getPassword()));
        user.setNickname(request.getShopName()); // 商家昵称默认为店铺名
        user.setRole("MERCHANT");
        user.setStatus(1);

        userMapper.insert(user);

        // 创建商家记录
        org.example.teasystem.entity.Merchant merchant = new org.example.teasystem.entity.Merchant();
        merchant.setUserId(user.getId());
        merchant.setShopName(request.getShopName());
        merchant.setShopDesc(request.getShopDesc());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setBusinessLicense(request.getBusinessLicense());
        merchant.setStatus(1); // 默认启用（实际可设为0待审核）

        merchantMapper.insert(merchant);

        log.info("商家注册成功: userId={}, merchantId={}, shopName={}",
                user.getId(), merchant.getId(), request.getShopName());

        return user.getId();
    }

    /**
     * 处理登录失败
     */
    private void handleLoginFail(String account) {
        String failKey = RedisConstants.LOGIN_FAIL_PREFIX + account;
        Long failCount = redisTemplate.opsForValue().increment(failKey);

        if (failCount == 1) {
            // 设置过期时间
            redisTemplate.expire(failKey, RedisConstants.LOGIN_LOCK_MINUTES, TimeUnit.MINUTES);
        }

        if (failCount != null && failCount >= RedisConstants.MAX_LOGIN_FAIL_COUNT) {
            // 锁定账号
            String lockKey = RedisConstants.ACCOUNT_LOCK_PREFIX + account;
            redisTemplate.opsForValue().set(lockKey, "1", RedisConstants.LOGIN_LOCK_MINUTES, TimeUnit.MINUTES);
            redisTemplate.delete(failKey);
            log.warn("账号登录失败次数过多，已锁定: account={}", account);
        }
    }

    @Override
    public LoginResponse smsLogin(org.example.teasystem.dto.auth.SmsLoginRequest request, String ip) {
        String phone = request.getPhone();
        String role = request.getRole() != null ? request.getRole() : "USER";

        // 验证验证码
        if (!smsService.verifyCode(phone, request.getCode(), "LOGIN")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误或已过期");
        }

        // 查询用户（按角色）
        User user = userMapper.findByPhoneAndRole(phone, role);

        if (user == null) {
            if (!"USER".equals(role)) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND, "该手机号未注册，请先注册");
            }
            user = new User();
            user.setPhone(phone);
            user.setUsername("user_" + phone.substring(7));
            user.setPasswordHash(PasswordUtil.md5("Tea@" + System.currentTimeMillis())); // 随机密码
            user.setNickname("用户" + phone.substring(7));
            user.setAvatar("/default-avatar.png"); // 设置默认头像
            user.setRole("USER");
            user.setStatus(1);
            userMapper.insert(user);
            log.info("短信登录自动注册用户: userId={}, phone={}", user.getId(), PasswordUtil.maskPhone(phone));
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 更新最后登录信息
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now(), ip);

        // 生成Token
        String token = jwtTokenProvider.createToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getRole());

        log.info("短信验证码登录成功: userId={}, phone={}, ip={}", user.getId(), PasswordUtil.maskPhone(phone), ip);

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .role(user.getRole())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phone(PasswordUtil.maskPhone(user.getPhone()))
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(org.example.teasystem.dto.auth.ResetPasswordRequest request) {
        String phone = request.getPhone();

        // 验证验证码
        if (!smsService.verifyCode(phone, request.getCode(), "RESET")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "验证码错误或已过期");
        }

        // 查询用户
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "该手机号未注册");
        }

        // 检查密码强度
        if (!PasswordUtil.checkPasswordStrength(request.getNewPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "密码必须包含字母和数字，长度8-20位");
        }

        // 更新密码
        userMapper.updatePassword(user.getId(), PasswordUtil.md5(request.getNewPassword()));

        log.info("密码重置成功: userId={}, phone={}", user.getId(), PasswordUtil.maskPhone(phone));
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 验证Refresh Token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "刷新令牌已过期或无效");
        }

        // 从Token中获取用户ID
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 生成新的Token
        String newAccessToken = jwtTokenProvider.createToken(userId, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, user.getRole());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getExpiration() / 1000)
                .role(user.getRole())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phone(PasswordUtil.maskPhone(user.getPhone()))
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    @Override
    public boolean checkUsernameExists(String username, String role) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userMapper.findByUsernameAndRole(username, role) != null;
    }

    @Override
    public boolean checkPhoneExists(String phone, String role) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return userMapper.findByPhoneAndRole(phone, role) != null;
    }
}
