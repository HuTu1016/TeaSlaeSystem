package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.response.OrderVO;
import org.example.teasystem.entity.Order;
import org.example.teasystem.entity.OrderItem;
import org.example.teasystem.mapper.OrderMapper;
import org.example.teasystem.mapper.UserMapper;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.entity.User;
import org.example.teasystem.entity.Merchant;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端订单管理控制器
 */
@Tag(name = "管理端-订单管理")
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageResult<OrderVO>> getOrderList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;
        
        int offset = (page - 1) * pageSize;
        List<Order> orders = orderMapper.selectByAdmin(status, payStatus, merchantId, userId, 
                orderNo, startDateTime, endDateTime, offset, pageSize);
        long total = orderMapper.countByAdmin(status, payStatus, merchantId, userId, 
                orderNo, startDateTime, endDateTime);
        
        List<OrderVO> voList = orders.stream().map(this::convertToVO).collect(Collectors.toList());
        
        PageResult<OrderVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        OrderVO vo = convertToVO(order);
        // 获取订单项
        List<OrderItem> items = orderMapper.selectOrderItems(orderId);
        vo.setItems(items);
        return Result.success(vo);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!"CREATED".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            return Result.fail("当前订单状态不允许取消");
        }
        orderMapper.updateCancelled(orderId, LocalDateTime.now());
        return Result.success();
    }

    @Operation(summary = "关闭订单")
    @PostMapping("/{orderId}/close")
    public Result<Void> closeOrder(@PathVariable Long orderId, @RequestParam(required = false) String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        orderMapper.updateStatus(orderId, "CLOSED");
        return Result.success();
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/stats")
    public Result<OrderStatsVO> getOrderStats() {
        OrderStatsVO stats = new OrderStatsVO();
        stats.setTotalOrders(orderMapper.countAll());
        stats.setTodayOrders(orderMapper.countToday());
        stats.setPendingOrders(orderMapper.countByAdmin("CREATED", null, null, null, null, null, null));
        stats.setPaidOrders(orderMapper.countByAdmin(null, "PAID", null, null, null, null, null));
        stats.setShippedOrders(orderMapper.countByAdmin("SHIPPED", null, null, null, null, null, null));
        stats.setCompletedOrders(orderMapper.countByAdmin("COMPLETED", null, null, null, null, null, null));
        return Result.success(stats);
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 获取用户信息
        if (order.getUserId() != null) {
            User user = userMapper.findById(order.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserPhone(user.getPhone());
            }
        }
        
        // 获取商家信息
        if (order.getMerchantId() != null) {
            Merchant merchant = merchantMapper.findById(order.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getShopName());
            }
        }
        
        return vo;
    }

    /**
     * 订单统计VO
     */
    @lombok.Data
    public static class OrderStatsVO {
        private Long totalOrders;
        private Long todayOrders;
        private Long pendingOrders;
        private Long paidOrders;
        private Long shippedOrders;
        private Long completedOrders;
    }
}
