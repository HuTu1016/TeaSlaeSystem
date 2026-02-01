package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项实体
 */
@Data
@Schema(description = "购物车项")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车项ID")
    private Long id;

    @Schema(description = "购物车ID")
    private Long cartId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "商品价格（加入购物车时的价格）")
    private BigDecimal price;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "是否选中：1是 0否")
    private Integer checked;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 获取选中状态（别名方法）
     */
    public Integer getSelected() {
        return this.checked;
    }

    /**
     * 设置选中状态（别名方法）
     */
    public void setSelected(Integer selected) {
        this.checked = selected;
    }
}
