package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.UserEvent;
import org.example.teasystem.security.SecurityUtils;
import org.example.teasystem.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐控制器
 */
@Tag(name = "推荐接口")
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendationService recommendationService;

    @Operation(summary = "首页推荐")
    @GetMapping("/home")
    public Result<List<Product>> getHomeRecommendations(
            @RequestParam(required = false) String deviceId,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        return Result.success(recommendationService.getHomeRecommendations(userId, deviceId, limit));
    }

    @Operation(summary = "热门商品")
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(recommendationService.getHotProducts(limit));
    }

    @Operation(summary = "新品推荐")
    @GetMapping("/new")
    public Result<List<Product>> getNewProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(recommendationService.getNewProducts(limit));
    }

    @Operation(summary = "相似商品")
    @GetMapping("/similar/{productId}")
    public Result<List<Product>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "6") Integer limit) {
        return Result.success(recommendationService.getSimilarProducts(productId, limit));
    }

    @Operation(summary = "猜你喜欢")
    @GetMapping("/guess")
    public Result<List<Product>> getGuessYouLike(
            @RequestParam(required = false) String deviceId,
            @RequestParam(defaultValue = "10") Integer limit) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();
        return Result.success(recommendationService.getGuessYouLike(userId, deviceId, limit));
    }

    @Operation(summary = "热门搜索词")
    @GetMapping("/hot-keywords")
    public Result<List<String>> getHotKeywords(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(recommendationService.getHotKeywords(limit));
    }

    @Operation(summary = "上报用户行为")
    @PostMapping("/event")
    public Result<Void> trackEvent(@RequestBody EventRequest request) {
        Long userId = SecurityUtils.getCurrentUserIdOrNull();

        UserEvent event = new UserEvent();
        event.setUserId(userId);
        event.setDeviceId(request.getDeviceId());
        event.setEventType(request.getEventType());
        event.setProductId(request.getProductId());
        event.setCategoryId(request.getCategoryId());
        event.setKeyword(request.getKeyword());

        recommendationService.trackEvent(event);
        return Result.success();
    }

    @Data
    public static class EventRequest {
        private String deviceId;
        /**
         * VIEW/CART/ORDER/SEARCH
         */
        private String eventType;
        private Long productId;
        private Long categoryId;
        private String keyword;
    }
}
