package org.example.teasystem.dto.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品查询请求
 */
@Data
public class ProductQueryRequest {

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 产地
     */
    private String origin;

    /**
     * 工艺
     */
    private String process;

    /**
     * 最低价
     */
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    private BigDecimal maxPrice;

    /**
     * 排序：sales_desc/price_asc/price_desc/newest
     */
    private String sort;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
