package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.product.ProductDetailVO;
import org.example.teasystem.dto.product.ProductListVO;
import org.example.teasystem.dto.product.ProductQueryRequest;
import org.example.teasystem.entity.Category;
import org.example.teasystem.mapper.CategoryMapper;
import org.example.teasystem.service.ProductService;
import org.example.teasystem.service.ReviewService;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.ReviewVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器（用户端）
 */
@Tag(name = "商品接口")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryMapper categoryMapper;
    private final org.example.teasystem.service.FavoriteService favoriteService;
    private final ReviewService reviewService;

    @Operation(summary = "获取商品列表")
    @GetMapping
    public Result<PageResult<ProductListVO>> getProductList(ProductQueryRequest request) {
        PageResult<ProductListVO> result = productService.getProductList(request);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        ProductDetailVO detail = productService.getProductDetail(id);

        // 记录足迹 (仅当用户已登录时)
        Long userId = org.example.teasystem.security.SecurityUtils.getCurrentUserIdOrNull();
        if (userId != null) {
            try {
                favoriteService.addFootprint(userId, id);
            } catch (Exception e) {
                // 忽略记录足迹异常，避免影响详情展示
                e.printStackTrace();
            }
        }

        return Result.success(detail);
    }

    @Operation(summary = "获取热门商品")
    @GetMapping("/hot")
    public Result<List<ProductListVO>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<ProductListVO> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    @Operation(summary = "获取新品")
    @GetMapping("/new")
    public Result<List<ProductListVO>> getNewProducts(@RequestParam(defaultValue = "10") Integer limit) {
        List<ProductListVO> products = productService.getNewProducts(limit);
        return Result.success(products);
    }

    @Operation(summary = "获取相似商品推荐")
    @GetMapping("/{id}/similar")
    public Result<List<ProductListVO>> getSimilarProducts(@PathVariable Long id,
            @RequestParam(defaultValue = "12") Integer limit) {
        List<ProductListVO> products = productService.getSimilarProducts(id, limit);
        return Result.success(products);
    }

    @Operation(summary = "获取商品评论列表")
    @GetMapping("/{id}/reviews")
    public Result<PageResponse<ReviewVO>> getProductReviews(@PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResponse<ReviewVO> result = reviewService.getProductReviews(id, page, size);
        return Result.success(result);
    }
}
