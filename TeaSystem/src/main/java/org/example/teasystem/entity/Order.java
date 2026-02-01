package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@Schema(description = "订单信息")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "订单状态：CREATED/PAID/CANCELLED/SHIPPED/COMPLETED/CLOSED")
    private String status;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "优惠券抵扣金额")
    private BigDecimal couponDiscountAmount;

    @Schema(description = "支付状态：UNPAID/PAYING/PAID/FAILED/REFUNDED")
    private String payStatus;

    @Schema(description = "地址快照（JSON）")
    private String addressSnapshot;

    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "支付时间")
    private LocalDateTime paidAt;

    @Schema(description = "发货时间")
    private LocalDateTime shippedAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;

    @Schema(description = "取消时间")
    private LocalDateTime cancelledAt;
}
