package org.example.teasystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsConfig {

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板Code
     */
    private String templateCode;

    /**
     * 开发模式（true时使用超级验证码，不实际发送短信）
     */
    private boolean devMode = false;

    /**
     * 超级验证码（开发模式下使用）
     */
    private String superCode = "1234";
}
