package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车VO
 */
@Data
public class CartVO {

    /**
     * 购物车ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 购物车商品列表
     */
    private List<CartItemVO> items;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 选中商品数量
     */
    private Integer checkedCount;

    /**
     * 商品总数量
     */
    private Integer totalQuantity;

    /**
     * 购物车项VO
     */
    @Data
    public static class CartItemVO {
        /**
         * 购物车项ID
         */
        private Long id;

        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品标题（别名）
         */
        private String productTitle;

        /**
         * SKU名称
         */
        private String skuName;

        /**
         * 商品图片
         */
        private String productImage;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer quantity;

        /**
         * 小计
         */
        private BigDecimal subtotal;

        /**
         * 是否选中
         */
        private Boolean selected;

        /**
         * 是否选中（别名，兼容checked）
         */
        private Boolean checked;

        /**
         * 是否有效（商品是否可购买）
         */
        private Boolean available;

        /**
         * 是否有效（别名）
         */
        private Boolean valid;

        /**
         * 库存
         */
        private Integer stock;
    }
}
