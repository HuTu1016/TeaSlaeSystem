package org.example.teasystem.dto.request;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商品查询请求
 */
@Data
public class ProductQueryRequest {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 产地
     */
    private String origin;

    /**
     * 工艺
     */
    private String process;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 排序字段：price, sales, time
     */
    private String sortBy = "time";

    /**
     * 排序方向：asc, desc
     */
    private String sortOrder = "desc";

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
