package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.ProductTrace;
import org.example.teasystem.service.TraceService;
import org.springframework.web.bind.annotation.*;

/**
 * 溯源控制器（用户端）
 */
@Tag(name = "溯源查询接口")
@RestController
@RequestMapping("/trace")
@RequiredArgsConstructor
public class TraceController {

    private final TraceService traceService;

    @Operation(summary = "通过溯源码查询")
    @GetMapping("/code/{traceCode}")
    public Result<ProductTrace> getByTraceCode(@PathVariable String traceCode) {
        return Result.success(traceService.getByTraceCode(traceCode));
    }

    @Operation(summary = "通过商品ID查询溯源")
    @GetMapping("/product/{productId}")
    public Result<ProductTrace> getByProductId(@PathVariable Long productId) {
        return Result.success(traceService.getByProductId(productId));
    }
}
