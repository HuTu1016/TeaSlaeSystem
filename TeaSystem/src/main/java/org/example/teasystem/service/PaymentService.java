package org.example.teasystem.service;

import org.example.teasystem.dto.payment.PaymentCreateResponse;
import org.example.teasystem.dto.payment.PaymentNotifyRequest;
import org.example.teasystem.dto.payment.PaymentVO;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 创建支付单
     */
    PaymentCreateResponse createPayment(Long userId, Long orderId);
    
    /**
     * 支付回调（Mock）
     */
    void notify(String payNo, PaymentNotifyRequest request);
    
    /**
     * 查询支付信息
     */
    PaymentVO getPaymentByOrderId(Long orderId);
}
