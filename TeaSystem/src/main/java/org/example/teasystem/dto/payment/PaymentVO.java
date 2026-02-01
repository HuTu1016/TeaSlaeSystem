package org.example.teasystem.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付信息VO
 */
@Data
public class PaymentVO {
    
    private Long id;
    private String payNo;
    private Long orderId;
    private String channel;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
