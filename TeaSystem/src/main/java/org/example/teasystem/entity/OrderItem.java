package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体
 */
@Data
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品标题快照
     */
    private String titleSnapshot;

    /**
     * SKU名称快照
     */
    private String skuNameSnapshot;

    /**
     * 图片快照
     */
    private String imageSnapshot;

    /**
     * 商品名称（创建订单时用）
     */
    private String productName;

    /**
     * SKU名称（创建订单时用）
     */
    private String skuName;

    /**
     * 商品图片（创建订单时用）
     */
    private String productImage;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计
     */
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
