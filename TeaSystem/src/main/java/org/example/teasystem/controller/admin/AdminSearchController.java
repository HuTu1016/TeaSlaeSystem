package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.service.ProductIndexService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索索引管理控制器（管理端）
 * 仅在search.use-elasticsearch=true时启用
 */
@Tag(name = "管理端-搜索索引管理")
@RestController
@RequestMapping("/admin/search")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@ConditionalOnProperty(name = "search.use-elasticsearch", havingValue = "true")
public class AdminSearchController {

    private final ProductIndexService productIndexService;

    @Operation(summary = "全量重建索引")
    @PostMapping("/reindex")
    public Result<Void> reindexAll() {
        productIndexService.reindexAll();
        return Result.success();
    }

    @Operation(summary = "索引单个商品")
    @PostMapping("/index/{productId}")
    public Result<Void> indexProduct(@PathVariable Long productId) {
        productIndexService.indexProduct(productId);
        return Result.success();
    }

    @Operation(summary = "删除商品索引")
    @DeleteMapping("/index/{productId}")
    public Result<Void> deleteProductIndex(@PathVariable Long productId) {
        productIndexService.deleteProductIndex(productId);
        return Result.success();
    }
}
