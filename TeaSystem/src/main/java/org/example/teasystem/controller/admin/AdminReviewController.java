package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.request.ReviewAuditRequest;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.ReviewVO;
import org.example.teasystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员评论审核控制器
 */
@Tag(name = "管理端-评论审核")
@RestController
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "获取待审核评论列表")
    @GetMapping("/pending")
    public Result<PageResponse<ReviewVO>> getPendingReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(reviewService.getPendingReviews(page, size));
    }

    @Operation(summary = "审核评论")
    @PutMapping("/{reviewId}/audit")
    public Result<Void> auditReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewAuditRequest request) {
        reviewService.auditReview(reviewId, request);
        return Result.success();
    }

    @Operation(summary = "获取所有评论列表")
    @GetMapping
    public Result<PageResponse<ReviewVO>> getAllReviews(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(reviewService.getReviewsByAdmin(status, productId, page, size));
    }

    @Operation(summary = "删除违规评论")
    @DeleteMapping("/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return Result.success();
    }
}
