package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.service.StatsService;
import org.example.teasystem.service.StatsService.AdminStatsVO;
import org.example.teasystem.service.StatsService.DailySalesVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理员统计控制器
 */
@Tag(name = "管理端-数据统计")
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final StatsService statsService;

    @Operation(summary = "获取平台统计概览")
    @GetMapping("/overview")
    public Result<AdminStatsVO> getOverview() {
        return Result.success(statsService.getAdminStats());
    }

    @Operation(summary = "获取平台销售趋势")
    @GetMapping("/trend")
    public Result<List<DailySalesVO>> getSalesTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getPlatformSalesTrend(startDate, endDate));
    }
}
