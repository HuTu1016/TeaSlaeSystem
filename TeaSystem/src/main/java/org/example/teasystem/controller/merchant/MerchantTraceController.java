package org.example.teasystem.controller.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.entity.ProductTrace;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.service.TraceService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 溯源管理控制器（商家端）
 */
@Tag(name = "商家端-溯源管理")
@RestController
@RequestMapping("/merchant/trace")
@RequiredArgsConstructor
public class MerchantTraceController {

    private final TraceService traceService;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取商家溯源列表")
    @GetMapping("/list")
    public Result<PageResult<ProductTrace>> getList(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        return Result.success(traceService.getMerchantTraceList(merchant.getId(), status, page, pageSize));
    }

    @Operation(summary = "创建溯源")
    @PostMapping
    public Result<Long> createTrace(Authentication authentication, @RequestBody ProductTrace trace) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        trace.setMerchantId(merchant.getId());
        Long id = traceService.createTrace(trace);
        return Result.success(id);
    }

    @Operation(summary = "更新溯源")
    @PutMapping("/{id}")
    public Result<Void> updateTrace(Authentication authentication, @PathVariable Long id,
            @RequestBody ProductTrace trace) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        trace.setId(id);
        trace.setMerchantId(merchant.getId());
        traceService.updateTrace(trace);
        return Result.success();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit")
    public Result<Void> submitForAudit(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        traceService.submitForAudit(merchant.getId(), id);
        return Result.success();
    }

    @Operation(summary = "删除溯源")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTrace(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        if (merchant == null) {
            return Result.fail("商家信息不存在");
        }
        traceService.deleteTrace(merchant.getId(), id);
        return Result.success();
    }
}
