package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体
 */
@Data
@Schema(description = "优惠券")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券ID")
    private Long id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "类型：AMOUNT-满减券, PERCENT-折扣券")
    private String type;

    @Schema(description = "优惠金额（满减券）")
    private BigDecimal amount;

    @Schema(description = "折扣比例（折扣券，如90表示9折）")
    private Integer discountPercent;

    @Schema(description = "使用门槛金额（满多少可用）")
    private BigDecimal minAmount;

    @Schema(description = "最大抵扣金额（折扣券）")
    private BigDecimal maxDiscount;

    @Schema(description = "总发行量")
    private Integer totalCount;

    @Schema(description = "已领取数量")
    private Integer receivedCount;

    @Schema(description = "每人限领数量")
    private Integer limitPerUser;

    @Schema(description = "有效天数（领取后N天有效）")
    private Integer validDays;

    @Schema(description = "固定开始时间")
    private LocalDateTime validFrom;

    @Schema(description = "固定结束时间")
    private LocalDateTime validTo;

    @Schema(description = "状态：ACTIVE-可用, INACTIVE-停用")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
