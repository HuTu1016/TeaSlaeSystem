package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@Schema(description = "商品评论")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单项ID")
    private Long orderItemId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "是否匿名：1是 0否")
    private Integer anonymous;

    @Schema(description = "评分1-5")
    private Integer rating;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "图片列表（JSON）")
    private String images;

    @Schema(description = "审核状态：PENDING/APPROVED/REJECTED")
    private String status;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
