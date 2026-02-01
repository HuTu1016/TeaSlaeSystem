package org.example.teasystem.dto.address;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址返回对象
 */
@Data
public class AddressVO {

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 是否默认 (0-否, 1-是)
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
