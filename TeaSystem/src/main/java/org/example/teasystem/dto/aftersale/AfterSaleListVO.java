package org.example.teasystem.dto.aftersale;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后列表VO
 */
@Data
public class AfterSaleListVO {

    private Long id;
    private String afterSaleNo;
    private String type;
    private String status;
    private String reason;
    private BigDecimal applyAmount;
    private LocalDateTime createdAt;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;
}
