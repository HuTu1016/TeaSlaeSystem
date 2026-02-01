package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车实体
 */
@Data
@Schema(description = "购物车")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
