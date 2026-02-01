package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付单号
     */
    private String payNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付渠道：MOCK/ALIPAY/WECHAT
     */
    private String channel;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    /**
     * 支付状态：CREATED/PAID/FAILED/REFUNDED
     */
    private String status;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 回调原文
     */
    private String notifyRaw;

    /**
     * 回调时间
     */
    private LocalDateTime notifyAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 支付时间
     */
    private LocalDateTime paidAt;

    // ============ 别名方法（兼容ServiceImpl中的调用）============

    /**
     * 获取支付单号（别名）
     */
    public String getPaymentNo() {
        return this.payNo;
    }

    /**
     * 设置支付单号（别名）
     */
    public void setPaymentNo(String paymentNo) {
        this.payNo = paymentNo;
    }

    /**
     * 获取支付方式（别名，返回channel）
     */
    public String getPaymentMethod() {
        return this.channel;
    }

    /**
     * 设置支付方式（别名，设置channel）
     */
    public void setPaymentMethod(String paymentMethod) {
        this.channel = paymentMethod;
    }
}
