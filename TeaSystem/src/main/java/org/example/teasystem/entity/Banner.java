package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 首页Banner实体
 */
@Data
@Schema(description = "首页Banner")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Banner ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "链接类型：PRODUCT-商品, ARTICLE-文章, URL-外链")
    private String linkType;

    @Schema(description = "链接值（商品ID/文章ID/外链URL）")
    private String linkValue;

    @Schema(description = "排序值（越小越靠前）")
    private Integer sort;

    @Schema(description = "状态：1-启用, 0-禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
