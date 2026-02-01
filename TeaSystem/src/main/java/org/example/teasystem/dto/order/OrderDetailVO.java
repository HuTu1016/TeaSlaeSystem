package org.example.teasystem.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 */
@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private String status;
    private String payStatus;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal couponDiscountAmount;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime completedAt;

    /**
     * 收货人姓名（前端直接使用）
     */
    private String receiverName;

    /**
     * 收货人电话（前端直接使用）
     */
    private String receiverPhone;

    /**
     * 完整收货地址（前端直接使用）
     */
    private String addressDetail;

    /**
     * 收货地址快照
     */
    private AddressSnapshot address;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> items;

    /**
     * 物流信息
     */
    private ShipmentVO shipment;

    @Data
    public static class AddressSnapshot {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detail;
    }

    @Data
    public static class OrderItemVO {
        private Long id;
        private Long productId;
        private Long skuId;
        private String titleSnapshot;
        private String skuNameSnapshot;
        private String imageSnapshot;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;

        // 前端兼容字段
        private String title; // 对应 titleSnapshot
        private String image; // 对应 imageSnapshot
        private String skuName; // 对应 skuNameSnapshot
    }

    @Data
    public static class ShipmentVO {
        private String carrier;
        private String trackingNo;
        private String trace;
    }
}
