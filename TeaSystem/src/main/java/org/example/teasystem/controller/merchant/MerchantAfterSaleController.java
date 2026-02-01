package org.example.teasystem.controller.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.aftersale.AfterSaleListVO;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.service.AfterSaleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 商家售后管理控制器
 */
@Tag(name = "商家-售后管理")
@RestController
@RequestMapping("/merchant/after-sales")
@RequiredArgsConstructor
public class MerchantAfterSaleController {

    private final AfterSaleService afterSaleService;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取售后列表")
    @GetMapping
    public Result<PageResult<AfterSaleListVO>> getAfterSales(Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        PageResult<AfterSaleListVO> result = afterSaleService.getMerchantAfterSales(merchant.getId(), status, page,
                pageSize);
        return Result.success(result);
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(Authentication authentication,
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        afterSaleService.approveAfterSale(merchant.getId(), id, comment);
        return Result.success();
    }

    @Operation(summary = "拒绝")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(Authentication authentication,
            @PathVariable Long id,
            @RequestParam String comment) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        afterSaleService.rejectAfterSale(merchant.getId(), id, comment);
        return Result.success();
    }

    @Operation(summary = "确认收货（退货退款）")
    @PostMapping("/{id}/confirm-received")
    public Result<Void> confirmReceived(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        afterSaleService.confirmReceived(merchant.getId(), id);
        return Result.success();
    }

    @Operation(summary = "执行退款")
    @PostMapping("/{id}/refund")
    public Result<Void> refund(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        afterSaleService.refund(merchant.getId(), id);
        return Result.success();
    }
}
