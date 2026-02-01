package org.example.teasystem.dto.address;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 地址保存请求（新增/编辑复用）
 */
@Data
public class AddressSaveRequest {

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    /**
     * 省
     */
    @NotBlank(message = "省不能为空")
    private String province;

    /**
     * 市
     */
    @NotBlank(message = "市不能为空")
    private String city;

    /**
     * 区
     */
    @NotBlank(message = "区不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /**
     * 是否设为默认 (0-否, 1-是)
     */
    private Integer isDefault = 0;
}
