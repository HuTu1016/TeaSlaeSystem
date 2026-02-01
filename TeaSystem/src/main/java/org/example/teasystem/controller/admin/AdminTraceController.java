package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ProductTrace;
import org.example.teasystem.service.TraceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 溯源审核控制器（管理端）
 */
@Tag(name = "管理端-溯源审核")
@RestController
@RequestMapping("/admin/trace")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTraceController {

    private final TraceService traceService;

    @Operation(summary = "获取溯源列表")
    @GetMapping("/list")
    public Result<PageResult<ProductTrace>> getList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(traceService.getAuditList(status, page, pageSize));
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        traceService.approve(id);
        return Result.success();
    }

    @Operation(summary = "审核拒绝")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody RejectRequest request) {
        traceService.reject(id, request.getReason());
        return Result.success();
    }

    @lombok.Data
    public static class RejectRequest {
        private String reason;
    }
}
