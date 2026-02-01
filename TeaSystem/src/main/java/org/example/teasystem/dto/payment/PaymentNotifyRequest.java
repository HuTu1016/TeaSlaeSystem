package org.example.teasystem.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付回调请求
 */
@Data
public class PaymentNotifyRequest {
    
    private BigDecimal amount;
    private LocalDateTime paidAt;
}
