package org.example.teasystem.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单创建请求
 */
@Data
public class OrderCreateRequest {

    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /**
     * 购物车项ID列表
     */
    private List<Long> cartItemIds;

    /**
     * 订单项列表（直接下单时使用）
     */
    private List<OrderItemRequest> items;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单项请求
     */
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "SKU ID不能为空")
        private Long skuId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
