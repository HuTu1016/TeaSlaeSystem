package org.example.teasystem.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支出明细VO
 * 用于会员中心页面展示用户的订单支付/退款记录
 */
@Data
public class OrderExpenseVO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 金额（正数为支出，负数为退款收入）
     */
    private BigDecimal amount;

    /**
     * 交易类型：PAYMENT-支付，REFUND-退款
     */
    private String expenseType;

    /**
     * 退款类型（仅退款时有值）：REFUND_ONLY-仅退款，RETURN_REFUND-退货退款
     */
    private String refundType;

    /**
     * 显示备注
     */
    private String remark;

    /**
     * 商品标题快照（订单第一个商品）
     */
    private String productTitle;

    /**
     * 商品图片快照
     */
    private String productImage;

    /**
     * 交易时间
     */
    private LocalDateTime createdAt;
}
