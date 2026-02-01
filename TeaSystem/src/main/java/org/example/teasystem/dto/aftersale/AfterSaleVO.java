package org.example.teasystem.dto.aftersale;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 售后详情VO
 */
@Data
public class AfterSaleVO {

    private Long id;
    private String afterSaleNo;
    private Long orderId;
    private Long orderItemId;
    private String type;
    private String status;
    private String reason;
    private String description;
    private List<String> images;
    private BigDecimal applyAmount;
    private String merchantComment;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;
}
