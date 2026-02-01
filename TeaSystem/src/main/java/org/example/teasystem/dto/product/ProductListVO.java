package org.example.teasystem.dto.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品列表VO
 */
@Data
public class ProductListVO {

    private Long id;
    private String title;
    private String subtitle;
    private String origin;
    private String process;
    private String taste;
    private String mainImage;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer salesCount;
    private String status;
}
