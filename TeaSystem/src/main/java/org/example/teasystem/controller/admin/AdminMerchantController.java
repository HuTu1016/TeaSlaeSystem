package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.response.MerchantVO;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员商家管理控制器
 */
@Tag(name = "管理端-商家管理")
@RestController
@RequestMapping("/admin/merchants")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;

    @Operation(summary = "获取商家列表")
    @GetMapping
    public Result<PageResponse<MerchantVO>> getMerchantList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(merchantService.getMerchantList(keyword, status, page, size));
    }

    @Operation(summary = "获取商家详情")
    @GetMapping("/{merchantId}")
    public Result<MerchantVO> getMerchantDetail(@PathVariable Long merchantId) {
        return Result.success(merchantService.getMerchantDetail(merchantId));
    }

    @Operation(summary = "审核商家入驻")
    @PutMapping("/{merchantId}/audit")
    public Result<Void> auditMerchant(
            @PathVariable Long merchantId,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason) {
        merchantService.auditMerchant(merchantId, status, reason);
        return Result.success();
    }

    @Operation(summary = "禁用商家")
    @PutMapping("/{merchantId}/disable")
    public Result<Void> disableMerchant(@PathVariable Long merchantId) {
        merchantService.updateMerchantStatus(merchantId, 0);
        return Result.success();
    }

    @Operation(summary = "启用商家")
    @PutMapping("/{merchantId}/enable")
    public Result<Void> enableMerchant(@PathVariable Long merchantId) {
        merchantService.updateMerchantStatus(merchantId, 1);
        return Result.success();
    }

    @Operation(summary = "获取商家统计数据")
    @GetMapping("/{merchantId}/stats")
    public Result<org.example.teasystem.dto.response.MerchantStatsDTO> getMerchantStats(@PathVariable Long merchantId) {
        return Result.success(merchantService.getMerchantStats(merchantId));
    }
}
