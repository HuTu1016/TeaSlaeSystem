package org.example.teasystem.dto.order;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateRequest {
    
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    
    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    /**
     * 优惠券ID（可选）
     */
    private Long couponId;
    
    /**
     * 订单备注
     */
    private String remark;
    
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "SKU ID不能为空")
        private Long skuId;
        
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
