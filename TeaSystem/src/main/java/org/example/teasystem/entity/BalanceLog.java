package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额变动明细实体
 */
@Data
@Schema(description = "余额变动明细")
public class BalanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "交易类型：RECHARGE-充值 REFUND-退款 WITHDRAW-提现 PAYMENT-支付 ADJUSTMENT-调整")
    private String type;

    @Schema(description = "变动金额（正数收入，负数支出）")
    private BigDecimal amount;

    @Schema(description = "变动前余额")
    private BigDecimal balanceBefore;

    @Schema(description = "变动后余额")
    private BigDecimal balanceAfter;

    @Schema(description = "状态：PENDING-处理中 SUCCESS-成功 FAILED-失败")
    private String status;

    @Schema(description = "关联业务类型")
    private String bizType;

    @Schema(description = "关联业务ID")
    private String bizId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
