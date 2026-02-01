package org.example.teasystem.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车VO
 */
@Data
public class CartVO {

    /**
     * 购物车项列表
     */
    private List<CartItemVO> items;

    /**
     * 选中商品总数
     */
    private Integer checkedCount;

    /**
     * 选中商品总金额
     */
    private BigDecimal checkedAmount;

    @Data
    public static class CartItemVO {
        private Long id;
        private Long skuId;
        private Long productId;
        private String productTitle;
        private String subtitle;
        private String skuName;
        private String image;
        private BigDecimal price;
        private Integer stock;
        private Integer quantity;
        private Integer checked;
        /**
         * 是否有效（商品下架或SKU禁用时为false）
         */
        private Boolean valid;
    }
}
