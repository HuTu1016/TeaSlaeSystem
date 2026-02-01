package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Product;
import org.example.teasystem.security.SecurityUtils;
import org.example.teasystem.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏和足迹控制器（用户端）
 */
@Tag(name = "收藏与足迹接口")
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // ==================== 收藏 ====================

    @Operation(summary = "获取我的收藏")
    @GetMapping("/favorites")
    public Result<PageResult<Product>> getFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(favoriteService.getFavorites(userId, page, pageSize));
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/favorites/{productId}/check")
    public Result<Boolean> checkFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(favoriteService.isFavorite(userId, productId));
    }

    @Operation(summary = "添加收藏")
    @PostMapping("/favorites/{productId}")
    public Result<Void> addFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteService.addFavorite(userId, productId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorites/{productId}")
    public Result<Void> removeFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteService.removeFavorite(userId, productId);
        return Result.success();
    }

    // ==================== 足迹 ====================

    @Operation(summary = "获取我的足迹")
    @GetMapping("/footprints")
    public Result<PageResult<Product>> getFootprints(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(favoriteService.getFootprints(userId, page, pageSize));
    }

    @Operation(summary = "删除足迹")
    @DeleteMapping("/footprints/{productId}")
    public Result<Void> removeFootprint(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteService.removeFootprint(userId, productId);
        return Result.success();
    }

    @Operation(summary = "清空足迹")
    @DeleteMapping("/footprints")
    public Result<Void> clearFootprints() {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteService.clearFootprints(userId);
        return Result.success();
    }
}
