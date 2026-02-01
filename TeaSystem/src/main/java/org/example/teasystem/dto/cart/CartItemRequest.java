package org.example.teasystem.dto.cart;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 购物车项请求
 */
@Data
public class CartItemRequest {
    
    @NotNull(message = "SKU ID不能为空")
    private Long skuId;
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量最少为1")
    @Max(value = 999, message = "数量最多为999")
    private Integer quantity;
}
