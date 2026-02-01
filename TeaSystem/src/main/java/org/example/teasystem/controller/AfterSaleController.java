package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.aftersale.*;
import org.example.teasystem.service.AfterSaleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 售后控制器（用户端）
 */
@Tag(name = "售后接口")
@RestController
@RequestMapping("/after-sales")
@RequiredArgsConstructor
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    @Operation(summary = "申请售后")
    @PostMapping
    public Result<Long> applyAfterSale(Authentication authentication,
            @Valid @RequestBody AfterSaleApplyRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Long afterSaleId = afterSaleService.applyAfterSale(userId, request);
        return Result.success(afterSaleId);
    }

    @Operation(summary = "获取售后列表")
    @GetMapping
    public Result<PageResult<AfterSaleListVO>> getAfterSales(Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        PageResult<AfterSaleListVO> result = afterSaleService.getUserAfterSales(userId, status, page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取售后详情")
    @GetMapping("/{id}")
    public Result<AfterSaleVO> getAfterSaleDetail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        AfterSaleVO detail = afterSaleService.getAfterSaleDetail(userId, id);
        return Result.success(detail);
    }

    @Operation(summary = "取消售后")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelAfterSale(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        afterSaleService.cancelAfterSale(userId, id);
        return Result.success();
    }

    @Operation(summary = "填写退货物流（退货退款场景）")
    @PostMapping("/{id}/ship")
    public Result<Void> shipBack(Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AfterSaleShipRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        afterSaleService.buyerShip(userId, id, request);
        return Result.success();
    }
}
