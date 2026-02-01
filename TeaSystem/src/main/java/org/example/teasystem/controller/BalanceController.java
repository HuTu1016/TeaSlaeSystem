package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.member.MemberCenterVO;
import org.example.teasystem.entity.BalanceLog;
import org.example.teasystem.entity.UserBalance;
import org.example.teasystem.service.BalanceService;
import org.example.teasystem.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户钱包/余额控制器
 */
@Tag(name = "用户钱包", description = "用户余额相关接口")
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final MemberService memberService;

    @Operation(summary = "获取钱包信息", description = "获取当前用户的钱包余额信息")
    @GetMapping
    public Result<UserBalance> getWallet(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserBalance balance = balanceService.getBalance(userId);
        return Result.success(balance);
    }

    @Operation(summary = "获取余额明细", description = "分页获取用户余额变动明细")
    @GetMapping("/logs")
    public Result<Map<String, Object>> getBalanceLogs(
            Authentication authentication,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        List<BalanceLog> logs = balanceService.getBalanceLogList(userId, type, page, size);
        int total = balanceService.countBalanceLogs(userId, type);

        Map<String, Object> result = new HashMap<>();
        result.put("list", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);

        return Result.success(result);
    }

    @Operation(summary = "获取钱包概览", description = "获取用户钱包余额和最近交易记录")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getWalletOverview(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserBalance balance = balanceService.getBalance(userId);
        List<BalanceLog> recentLogs = balanceService.getBalanceLogList(userId, null, 1, 5);

        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        result.put("recentLogs", recentLogs);

        return Result.success(result);
    }

    @Operation(summary = "获取会员中心信息", description = "获取用户钱包余额、会员等级、进度等完整信息")
    @GetMapping("/member-center")
    public Result<MemberCenterVO> getMemberCenter(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        MemberCenterVO memberCenter = memberService.getMemberCenterInfo(userId);
        return Result.success(memberCenter);
    }
}
