package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.order.*;
import org.example.teasystem.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器（用户端）
 */
@Tag(name = "订单接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderCreateResponse> createOrder(Authentication authentication,
            @Valid @RequestBody OrderCreateRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotentKey) {
        Long userId = (Long) authentication.getPrincipal();
        OrderCreateResponse response = orderService.createOrder(userId, request, idempotentKey);
        return Result.success(response);
    }

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageResult<OrderListVO>> getOrders(Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        PageResult<OrderListVO> result = orderService.getUserOrders(userId, status, page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(Authentication authentication, @PathVariable Long orderId) {
        Long userId = (Long) authentication.getPrincipal();
        OrderDetailVO detail = orderService.getOrderDetail(userId, orderId);
        return Result.success(detail);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(Authentication authentication, @PathVariable Long orderId) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{orderId}/confirm-receipt")
    public Result<Void> confirmReceipt(Authentication authentication, @PathVariable Long orderId) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.confirmReceipt(userId, orderId);
        return Result.success();
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{orderId}/pay")
    public Result<Void> payOrder(
            Authentication authentication,
            @PathVariable Long orderId,
            @RequestParam(value = "paymentMethod", defaultValue = "DIRECT") String paymentMethod) {
        Long userId = (Long) authentication.getPrincipal();
        orderService.payOrder(userId, orderId, paymentMethod);
        return Result.success();
    }

    @Operation(summary = "获取订单支出明细")
    @GetMapping("/expense-list")
    public Result<PageResult<OrderExpenseVO>> getOrderExpenseList(
            Authentication authentication,
            @RequestParam(value = "filterType", defaultValue = "ALL") String filterType,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        PageResult<OrderExpenseVO> result = orderService.getOrderExpenseList(userId, filterType, page, size);
        return Result.success(result);
    }
}
