package org.example.teasystem.controller.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.service.StatsService;
import org.example.teasystem.service.StatsService.DailySalesVO;
import org.example.teasystem.service.StatsService.MerchantStatsVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家统计控制器
 */
@Tag(name = "商家端-数据统计")
@RestController
@RequestMapping("/merchant/stats")
@RequiredArgsConstructor
public class MerchantStatsController {

    private final StatsService statsService;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取商家统计概览")
    @GetMapping("/overview")
    public Result<MerchantStatsVO> getOverview(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        return Result.success(statsService.getMerchantStats(merchant.getId()));
    }

    @Operation(summary = "获取销售趋势")
    @GetMapping("/trend")
    public Result<List<DailySalesVO>> getSalesTrend(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        return Result.success(statsService.getMerchantSalesTrend(merchant.getId(), startDate, endDate));
    }
}
