package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后实体
 */
@Data
public class AfterSale implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 售后ID
     */
    private Long id;
    
    /**
     * 售后单号
     */
    private String afterSaleNo;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单项ID
     */
    private Long orderItemId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 售后类型：REFUND_ONLY/RETURN_REFUND/EXCHANGE
     */
    private String type;
    
    /**
     * 售后状态
     */
    private String status;
    
    /**
     * 申请原因
     */
    private String reason;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 图片列表（JSON）
     */
    private String images;
    
    /**
     * 申请金额
     */
    private BigDecimal applyAmount;
    
    /**
     * 商家处理说明
     */
    private String merchantComment;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
