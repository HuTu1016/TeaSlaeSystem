package org.example.teasystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 待评价商品信息 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingReviewItemVO {

    /**
     * 订单 ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单项 ID
     */
    private Long orderItemId;

    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * SKU 名称
     */
    private String skuName;

    /**
     * 商品图片
     */
    private String image;
}
