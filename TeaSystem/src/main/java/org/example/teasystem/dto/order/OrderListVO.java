package org.example.teasystem.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表VO
 */
@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private String status;
    private String payStatus;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer itemCount;
    private LocalDateTime createdAt;

    /**
     * 是否有正在处理的售后
     */
    private Boolean hasAfterSale;

    /**
     * 订单商品项列表
     */
    private List<OrderItemVO> items;

    /**
     * 订单商品项VO
     */
    @Data
    public static class OrderItemVO {
        private Long id;
        private String title; // 商品标题
        private String skuName; // 规格名称
        private String image; // 商品图片
        private BigDecimal price; // 单价
        private Integer quantity; // 数量
        private Long afterSaleId; // 售后ID（如有正在处理的售后）
        private String afterSaleStatus; // 售后状态：APPLIED-已申请,APPROVED-已同意,REJECTED-已拒绝,COMPLETED-已完成,CANCELLED-已取消
    }
}
