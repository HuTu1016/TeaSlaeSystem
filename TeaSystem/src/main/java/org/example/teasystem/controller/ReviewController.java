package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.request.ReviewCreateRequest;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.PendingReviewItemVO;
import org.example.teasystem.dto.response.ReviewVO;
import org.example.teasystem.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 评价控制器（用户端）
 */
@Tag(name = "评价接口")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "提交评价")
    @PostMapping
    public Result<Void> createReview(Authentication authentication,
            @Valid @RequestBody ReviewCreateRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        reviewService.createReview(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取我的评价列表")
    @GetMapping
    public Result<PageResponse<ReviewVO>> getMyReviews(Authentication authentication,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        PageResponse<ReviewVO> result = reviewService.getUserReviews(userId, page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取待评价商品列表")
    @GetMapping("/pending-items")
    public Result<List<PendingReviewItemVO>> getPendingReviewItems(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<PendingReviewItemVO> items = reviewService.getPendingReviewItems(userId);
        return Result.success(items);
    }

    @Operation(summary = "获取订单评价状态", description = "返回已全部评价的订单ID列表")
    @GetMapping("/orders-review-status")
    public Result<List<Long>> getOrdersReviewStatus(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<Long> reviewedOrderIds = reviewService.getFullyReviewedOrderIds(userId);
        return Result.success(reviewedOrderIds);
    }
}
