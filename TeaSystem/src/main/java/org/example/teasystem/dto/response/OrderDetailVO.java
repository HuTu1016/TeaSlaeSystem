package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单状态文本
     */
    private String statusText;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> items;

    /**
     * 收货地址
     */
    private AddressVO address;

    /**
     * 物流信息
     */
    private ShipmentVO shipment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 订单项VO
     */
    @Data
    public static class OrderItemVO {
        private Long id;
        private Long productId;
        private Long skuId;
        private String productTitle;
        private String skuName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
        private Boolean canReview;
        private Boolean canAfterSale;
    }

    /**
     * 收货地址VO
     */
    @Data
    public static class AddressVO {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detail;
    }

    /**
     * 物流信息VO
     */
    @Data
    public static class ShipmentVO {
        private String carrier;
        private String trackingNo;
        private String trace;
        private LocalDateTime shipTime;
    }
}
