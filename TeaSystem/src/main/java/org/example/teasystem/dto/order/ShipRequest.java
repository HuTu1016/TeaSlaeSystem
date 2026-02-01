package org.example.teasystem.dto.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 发货请求
 */
@Data
public class ShipRequest {
    
    @NotBlank(message = "快递公司不能为空")
    @Size(max = 50, message = "快递公司最长50字符")
    private String carrier;
    
    @NotBlank(message = "快递单号不能为空")
    @Size(min = 6, max = 50, message = "快递单号长度6-50字符")
    private String trackingNo;
}
