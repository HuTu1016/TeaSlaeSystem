package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.mapper.*;
import org.example.teasystem.service.StatsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final AfterSaleMapper afterSaleMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;
    private final TraceMapper traceMapper;

    @Override
    public MerchantStatsVO getMerchantStats(Long merchantId) {
        MerchantStatsVO stats = new MerchantStatsVO();

        // 订单统计
        stats.setTotalOrders(orderMapper.countByMerchantId(merchantId, null));
        stats.setTodayOrders(countTodayOrdersByMerchant(merchantId));

        // 销售额统计
        stats.setTotalSales(calculateTotalSalesByMerchant(merchantId));
        stats.setTodaySales(calculateTodaySalesByMerchant(merchantId));

        // 商品统计
        stats.setTotalProducts((long) productMapper.countByMerchantId(merchantId, null));
        stats.setOnShelfProducts((long) productMapper.countByMerchantId(merchantId, "ON_SHELF"));

        // 待处理统计
        stats.setPendingAfterSales(afterSaleMapper.countByMerchantId(merchantId, "PENDING"));
        stats.setPendingReviews(countPendingReplyReviews(merchantId));

        return stats;
    }

    @Override
    public List<DailySalesVO> getMerchantSalesTrend(Long merchantId, LocalDate startDate, LocalDate endDate) {
        // 从数据库查询每日销售数据
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<java.util.Map<String, Object>> dbData = orderMapper.selectDailySalesByMerchantId(
                merchantId, startDateTime, endDateTime);

        // 构建日期到数据的映射
        java.util.Map<LocalDate, DailySalesVO> dataMap = new java.util.HashMap<>();
        for (java.util.Map<String, Object> row : dbData) {
            DailySalesVO daily = new DailySalesVO();
            Object dateObj = row.get("date");
            LocalDate date = dateObj instanceof java.sql.Date
                    ? ((java.sql.Date) dateObj).toLocalDate()
                    : LocalDate.parse(dateObj.toString());
            daily.setDate(date);
            daily.setOrderCount(((Number) row.get("orderCount")).longValue());
            daily.setSalesAmount((BigDecimal) row.get("salesAmount"));
            dataMap.put(date, daily);
        }

        // 填充所有日期（包括没有数据的日期）
        List<DailySalesVO> result = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            DailySalesVO daily = dataMap.getOrDefault(current, new DailySalesVO());
            if (daily.getDate() == null) {
                daily.setDate(current);
                daily.setOrderCount(0L);
                daily.setSalesAmount(BigDecimal.ZERO);
            }
            result.add(daily);
            current = current.plusDays(1);
        }
        return result;
    }

    @Override
    public AdminStatsVO getAdminStats() {
        AdminStatsVO stats = new AdminStatsVO();

        // 用户和商家统计
        stats.setTotalUsers(userMapper.countAll());
        stats.setTotalMerchants(merchantMapper.countAll());

        // 订单统计
        stats.setTotalOrders(countAllOrders());
        stats.setTodayOrders(countTodayOrders());

        // 销售额统计
        stats.setTotalSales(calculateTotalSales());
        stats.setTodaySales(calculateTodaySales());

        // 待审核统计
        stats.setPendingReviews(reviewMapper.countByStatus("PENDING"));
        stats.setPendingTraces(traceMapper.count("PENDING"));

        return stats;
    }

    @Override
    public List<DailySalesVO> getPlatformSalesTrend(LocalDate startDate, LocalDate endDate) {
        java.time.LocalDateTime startDateTime = startDate.atStartOfDay();
        java.time.LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<java.util.Map<String, Object>> dbData = orderMapper.selectDailySales(startDateTime, endDateTime);

        // 构建日期到数据的映射
        java.util.Map<LocalDate, DailySalesVO> dataMap = new java.util.HashMap<>();
        for (java.util.Map<String, Object> row : dbData) {
            DailySalesVO daily = new DailySalesVO();
            Object dateObj = row.get("date");
            LocalDate date = dateObj instanceof java.sql.Date
                    ? ((java.sql.Date) dateObj).toLocalDate()
                    : LocalDate.parse(dateObj.toString());
            daily.setDate(date);
            daily.setOrderCount(((Number) row.get("orderCount")).longValue());
            daily.setSalesAmount((BigDecimal) row.get("salesAmount"));
            dataMap.put(date, daily);
        }

        // 填充所有日期（包括没有数据的日期）
        List<DailySalesVO> result = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            DailySalesVO daily = dataMap.getOrDefault(current, new DailySalesVO());
            if (daily.getDate() == null) {
                daily.setDate(current);
                daily.setOrderCount(0L);
                daily.setSalesAmount(BigDecimal.ZERO);
            }
            result.add(daily);
            current = current.plusDays(1);
        }
        return result;
    }

    // ========== 私有辅助方法 ==========

    private Long countTodayOrdersByMerchant(Long merchantId) {
        Long count = orderMapper.countTodayByMerchantId(merchantId);
        return count != null ? count : 0L;
    }

    private BigDecimal calculateTotalSalesByMerchant(Long merchantId) {
        BigDecimal sales = orderMapper.sumSalesByMerchantId(merchantId);
        return sales != null ? sales : BigDecimal.ZERO;
    }

    private BigDecimal calculateTodaySalesByMerchant(Long merchantId) {
        BigDecimal sales = orderMapper.sumTodaySalesByMerchantId(merchantId);
        return sales != null ? sales : BigDecimal.ZERO;
    }

    private Long countPendingReplyReviews(Long merchantId) {
        return reviewMapper.countPendingReplyByMerchant(merchantId);
    }

    private Long countAllOrders() {
        Long count = orderMapper.countAll();
        return count != null ? count : 0L;
    }

    private Long countTodayOrders() {
        Long count = orderMapper.countToday();
        return count != null ? count : 0L;
    }

    private BigDecimal calculateTotalSales() {
        BigDecimal sales = orderMapper.sumAllSales();
        return sales != null ? sales : BigDecimal.ZERO;
    }

    private BigDecimal calculateTodaySales() {
        BigDecimal sales = orderMapper.sumTodaySales();
        return sales != null ? sales : BigDecimal.ZERO;
    }
}
