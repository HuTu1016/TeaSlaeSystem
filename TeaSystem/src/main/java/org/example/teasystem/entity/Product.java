package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@Schema(description = "商品信息")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商品标题")
    private String title;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "产地")
    private String origin;

    @Schema(description = "工艺")
    private String process;

    @Schema(description = "口感描述")
    private String taste;

    @Schema(description = "冲泡方法")
    private String brewMethod;

    @Schema(description = "产地环境")
    private String environment;

    @Schema(description = "主图URL")
    private String mainImage;

    @Schema(description = "状态：DRAFT/ON_SHELF/OFF_SHELF")
    private String status;

    @Schema(description = "最低价")
    private BigDecimal minPrice;

    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    @Schema(description = "销量")
    private Integer salesCount;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "审核备注")
    private String auditComment;

    @Schema(description = "审核时间")
    private LocalDateTime auditedAt;

    @Schema(description = "审核人ID")
    private Long auditedBy;
}
