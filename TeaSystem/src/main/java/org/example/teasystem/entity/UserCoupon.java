package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 */
@Data
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 状态：UNUSED-未使用, USED-已使用, EXPIRED-已过期
     */
    private String status;

    /**
     * 有效期开始
     */
    private LocalDateTime validFrom;

    /**
     * 有效期结束
     */
    private LocalDateTime validTo;

    /**
     * 使用时间
     */
    private LocalDateTime usedAt;

    /**
     * 使用的订单ID
     */
    private Long orderId;

    /**
     * 领取时间
     */
    private LocalDateTime createdAt;

    // ==================== 关联字段 (来自Coupon表) ====================

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 类型：AMOUNT-满减, PERCENT-折扣
     */
    private String type;

    /**
     * 优惠金额
     */
    private java.math.BigDecimal amount;

    /**
     * 折扣率 (0-100)
     */
    private Integer discountPercent;

    /**
     * 使用门槛
     */
    private java.math.BigDecimal minAmount;

    /**
     * 最大抵扣金额
     */
    private java.math.BigDecimal maxDiscount;
}
