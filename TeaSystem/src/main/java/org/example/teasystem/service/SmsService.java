package org.example.teasystem.service;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.config.SmsConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsConfig smsConfig;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 验证码Redis Key前缀
     */
    private static final String SMS_CODE_PREFIX = "tea:sms:code:";

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 发送验证码
     * 
     * @param phone 手机号
     * @param type  类型：LOGIN/REGISTER/RESET
     * @return 是否发送成功
     */
    public boolean sendVerificationCode(String phone, String type) {
        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 存储到Redis，格式：tea:sms:code:LOGIN:13800138000
        String key = SMS_CODE_PREFIX + type + ":" + phone;
        stringRedisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 开发模式：不实际发送短信，只打印日志
        if (smsConfig.isDevMode()) {
            log.info("【开发模式】验证码已生成 - 手机号: {}, 类型: {}, 验证码: {}, 超级验证码: {}",
                    phone, type, code, smsConfig.getSuperCode());
            return true;
        }

        // 生产模式：调用阿里云短信API
        try {
            sendAliyunSms(phone, code);
            log.info("短信发送成功 - 手机号: {}, 类型: {}", phone, type);
            return true;
        } catch (Exception e) {
            log.error("短信发送失败 - 手机号: {}, 错误: {}", phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证验证码
     * 
     * @param phone 手机号
     * @param code  用户输入的验证码
     * @param type  类型：LOGIN/REGISTER/RESET
     * @return 是否验证成功
     */
    public boolean verifyCode(String phone, String code, String type) {
        // 开发模式：超级验证码直接通过
        if (smsConfig.isDevMode() && smsConfig.getSuperCode().equals(code)) {
            log.info("【开发模式】使用超级验证码验证通过 - 手机号: {}, 类型: {}", phone, type);
            return true;
        }

        // 从Redis获取验证码
        String key = SMS_CODE_PREFIX + type + ":" + phone;
        String savedCode = stringRedisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            log.warn("验证码不存在或已过期 - 手机号: {}, 类型: {}", phone, type);
            return false;
        }

        if (savedCode.equals(code)) {
            // 验证成功后删除验证码
            stringRedisTemplate.delete(key);
            log.info("验证码验证成功 - 手机号: {}, 类型: {}", phone, type);
            return true;
        }

        log.warn("验证码错误 - 手机号: {}, 类型: {}", phone, type);
        return false;
    }

    /**
     * 调用阿里云短信API发送短信
     */
    private void sendAliyunSms(String phone, String code) throws Exception {
        // 创建阿里云短信客户端
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(smsConfig.getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAccessKeySecret())
                .setEndpoint("dysmsapi.aliyuncs.com");

        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

        // 构建发送请求
        com.aliyun.dysmsapi20170525.models.SendSmsRequest request = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsConfig.getSignName())
                .setTemplateCode(smsConfig.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}");

        // 发送短信
        com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSms(request);

        if (!"OK".equals(response.getBody().getCode())) {
            throw new RuntimeException("短信发送失败: " + response.getBody().getMessage());
        }
    }
}
