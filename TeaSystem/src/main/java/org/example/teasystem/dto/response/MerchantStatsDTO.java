package org.example.teasystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商家统计数据DTO
 * 用于管理后台展示商家维度的统计信息
 */
@Data
public class MerchantStatsDTO {

    // ========== 商品统计 ==========
    /**
     * 在售商品数量
     */
    private Long onShelfProducts;

    /**
     * 下架商品数量
     */
    private Long offShelfProducts;

    /**
     * 待审核商品数量
     */
    private Long pendingProducts;

    /**
     * 商品总数
     */
    private Long totalProducts;

    // ========== 订单统计 ==========
    /**
     * 订单总数
     */
    private Long totalOrders;

    /**
     * 待处理订单数（已支付待发货）
     */
    private Long pendingOrders;

    /**
     * 已发货订单数
     */
    private Long shippedOrders;

    /**
     * 已完成订单数
     */
    private Long completedOrders;

    /**
     * 总销售额
     */
    private BigDecimal totalSales;

    // ========== 售后统计 ==========
    /**
     * 售后总数
     */
    private Long totalAfterSales;

    /**
     * 待处理售后数
     */
    private Long pendingAfterSales;

    // ========== 评价统计 ==========
    /**
     * 评价总数
     */
    private Long totalReviews;

    /**
     * 待审核评价数
     */
    private Long pendingReviews;
}
