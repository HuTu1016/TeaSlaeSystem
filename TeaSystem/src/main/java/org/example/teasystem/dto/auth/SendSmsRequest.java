package org.example.teasystem.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码请求
 */
@Data
public class SendSmsRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 验证码类型：LOGIN/REGISTER/RESET
     */
    @NotBlank(message = "验证码类型不能为空")
    private String type;
}
