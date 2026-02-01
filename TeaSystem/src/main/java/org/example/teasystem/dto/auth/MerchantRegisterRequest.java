package org.example.teasystem.dto.auth;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 商家注册请求
 */
@Data
public class MerchantRegisterRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(min = 4, max = 20, message = "用户名长度4-20位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度8-20位")
    private String password;

    @NotBlank(message = "店铺名称不能为空")
    @Size(max = 50, message = "店铺名称最长50字符")
    private String shopName;

    @Size(max = 200, message = "店铺描述最长200字符")
    private String shopDesc;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 营业执照URL（可选，用于商家入驻审核）
     */
    private String businessLicense;
}
