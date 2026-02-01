package org.example.teasystem.dto.request;

import javax.validation.constraints.NotNull;

/**
 * 支付请求
 */
public class PaymentRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 支付方式：WECHAT-微信支付 ALIPAY-支付宝
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
