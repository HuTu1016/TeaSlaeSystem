package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表VO
 */
@Data
public class OrderVO {

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
     * 商品数量
     */
    private Integer itemCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 订单项列表
     */
    private java.util.List<org.example.teasystem.entity.OrderItem> items;

    /**
     * 收货地址快照
     */
    private String addressSnapshot;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 物流公司
     */
    private String trackingCompany;

    /**
     * 发货时间
     */
    private LocalDateTime shippedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
