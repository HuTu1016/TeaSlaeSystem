package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品列表VO
 */
@Data
public class ProductVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 封面图片
     */
    private String coverImage;

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
     * 销量
     */
    private Integer salesCount;

    /**
     * 状态：0-待审核，1-已上架，2-已下架
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
