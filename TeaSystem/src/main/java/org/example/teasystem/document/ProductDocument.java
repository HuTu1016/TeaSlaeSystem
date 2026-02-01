package org.example.teasystem.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品ES文档
 */
@Data
@Document(indexName = "tea_product_v1")
@Setting(settingPath = "/elasticsearch/product-settings.json")
public class ProductDocument {

    @Id
    private Long id;

    /**
     * 商品标题
     */
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String title;

    /**
     * 副标题
     */
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String subtitle;

    /**
     * 类目ID
     */
    @Field(type = FieldType.Long)
    private Long categoryId;

    /**
     * 类目名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 产地
     */
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String origin;

    /**
     * 工艺
     */
    @Field(type = FieldType.Keyword)
    private String process;

    /**
     * 口感
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String taste;

    /**
     * 商家ID
     */
    @Field(type = FieldType.Long)
    private Long merchantId;

    /**
     * 商家名称
     */
    @Field(type = FieldType.Keyword)
    private String merchantName;

    /**
     * 最低价
     */
    @Field(type = FieldType.Double)
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    @Field(type = FieldType.Double)
    private BigDecimal maxPrice;

    /**
     * 销量
     */
    @Field(type = FieldType.Integer)
    private Integer salesCount;

    /**
     * 主图
     */
    @Field(type = FieldType.Keyword, index = false)
    private String mainImage;

    /**
     * 状态
     */
    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * 是否有库存
     */
    @Field(type = FieldType.Boolean)
    private Boolean inStock;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    /**
     * 搜索建议（用于自动补全）
     */
    @CompletionField(analyzer = "standard", searchAnalyzer = "standard", maxInputLength = 100)
    private String suggest;
}
