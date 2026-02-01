package org.example.teasystem.controller.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.order.OrderListVO;
import org.example.teasystem.dto.order.ShipRequest;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商家订单管理控制器
 */
@Tag(name = "商家-订单管理")
@RestController
@RequestMapping("/merchant/orders")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageResult<OrderListVO>> getOrders(Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        PageResult<OrderListVO> result = orderService.getMerchantOrders(merchant.getId(), status, page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "发货")
    @PostMapping("/{orderId}/ship")
    public Result<Void> shipOrder(Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody ShipRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        orderService.shipOrder(merchant.getId(), orderId, request);
        return Result.success();
    }
}
