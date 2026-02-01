package org.example.teasystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatsService {

    /**
     * 获取商家统计概览
     */
    MerchantStatsVO getMerchantStats(Long merchantId);

    /**
     * 获取商家销售趋势（按天）
     */
    List<DailySalesVO> getMerchantSalesTrend(Long merchantId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取管理员平台统计概览
     */
    AdminStatsVO getAdminStats();

    /**
     * 获取平台销售趋势（按天）
     */
    List<DailySalesVO> getPlatformSalesTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 商家统计概览VO
     */
    @lombok.Data
    class MerchantStatsVO {
        private Long totalOrders; // 总订单数
        private Long todayOrders; // 今日订单
        private java.math.BigDecimal totalSales; // 总销售额
        private java.math.BigDecimal todaySales; // 今日销售额
        private Long totalProducts; // 商品总数
        private Long onShelfProducts; // 上架商品数
        private Long pendingAfterSales; // 待处理售后
        private Long pendingReviews; // 待回复评论
    }

    /**
     * 管理员平台统计概览VO
     */
    @lombok.Data
    class AdminStatsVO {
        private Long totalUsers; // 总用户数
        private Long totalMerchants; // 总商家数
        private Long totalOrders; // 总订单数
        private java.math.BigDecimal totalSales; // 总销售额
        private Long todayOrders; // 今日订单
        private java.math.BigDecimal todaySales; // 今日销售额
        private Long pendingReviews; // 待审核评论
        private Long pendingTraces; // 待审核溯源
    }

    /**
     * 每日销售数据VO
     */
    @lombok.Data
    class DailySalesVO {
        private LocalDate date;
        private Long orderCount;
        private java.math.BigDecimal salesAmount;
    }
}
