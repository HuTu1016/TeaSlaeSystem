package org.example.teasystem.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情VO
 */
@Data
public class ProductDetailVO {
    
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String subtitle;
    private String origin;
    private String process;
    private String taste;
    private String brewMethod;
    private String environment;
    private String mainImage;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer salesCount;
    private Integer viewCount;
    private String status;
    
    /**
     * 商品图片列表
     */
    private List<String> images;
    
    /**
     * SKU列表
     */
    private List<SkuVO> skus;
    
    @Data
    public static class SkuVO {
        private Long id;
        private String skuName;
        private BigDecimal price;
        private Integer stock;
        private Integer status;
    }
}
