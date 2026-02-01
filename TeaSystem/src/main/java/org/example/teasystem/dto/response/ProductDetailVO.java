package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情VO
 */
@Data
public class ProductDetailVO {

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
     * 产地
     */
    private String origin;

    /**
     * 工艺
     */
    private String process;

    /**
     * 口感描述
     */
    private String taste;

    /**
     * 冲泡方法
     */
    private String brewMethod;

    /**
     * 产地环境
     */
    private String environment;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 商品图片列表
     */
    private List<String> images;

    /**
     * SKU列表
     */
    private List<SkuVO> skuList;

    /**
     * 溯源信息摘要
     */
    private String traceSummary;

    /**
     * 溯源码
     */
    private String traceCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * SKU VO
     */
    @Data
    public static class SkuVO {
        private Long id;
        private String skuName;
        private BigDecimal price;
        private Integer stock;
        private Integer status;
    }
}
