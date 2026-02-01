package org.example.teasystem.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 创建订单响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResponse {
    
    private Long orderId;
    private String orderNo;
    private BigDecimal payAmount;
}
