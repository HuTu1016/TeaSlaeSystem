package org.example.teasystem.controller.merchant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.product.ProductDetailVO;
import org.example.teasystem.dto.product.ProductListVO;
import org.example.teasystem.dto.product.ProductSaveRequest;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商家商品管理控制器
 */
@Tag(name = "商家-商品管理")
@RestController
@RequestMapping("/merchant/products")
@RequiredArgsConstructor
public class MerchantProductController {

    private final ProductService productService;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取商家商品列表")
    @GetMapping
    public Result<PageResult<ProductListVO>> getProducts(Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        PageResult<ProductListVO> result = productService.getMerchantProducts(
                merchant.getId(), status, keyword, page, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<ProductDetailVO> getProductDetail(Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        ProductDetailVO detail = productService.getMerchantProductDetail(merchant.getId(), id);
        return Result.success(detail);
    }

    @Operation(summary = "创建商品")
    @PostMapping
    public Result<Long> createProduct(Authentication authentication,
            @Valid @RequestBody ProductSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        Long productId = productService.createProduct(merchant.getId(), request);
        return Result.success(productId);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public Result<Void> updateProduct(Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProductSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        productService.updateProduct(merchant.getId(), id, request);
        return Result.success();
    }

    @Operation(summary = "商品上架")
    @PostMapping("/{id}/on-shelf")
    public Result<Void> onShelf(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        productService.onShelf(merchant.getId(), id);
        return Result.success();
    }

    @Operation(summary = "商品下架")
    @PostMapping("/{id}/off-shelf")
    public Result<Void> offShelf(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        productService.offShelf(merchant.getId(), id);
        return Result.success();
    }

    // ==================== SKU管理 ====================

    @Operation(summary = "创建SKU")
    @PostMapping("/{productId}/skus")
    public Result<Long> createSku(Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody ProductSaveRequest.SkuSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        Long skuId = productService.createSku(merchant.getId(), productId, request);
        return Result.success(skuId);
    }

    @Operation(summary = "更新SKU")
    @PutMapping("/skus/{skuId}")
    public Result<Void> updateSku(Authentication authentication,
            @PathVariable Long skuId,
            @Valid @RequestBody ProductSaveRequest.SkuSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        productService.updateSku(merchant.getId(), skuId, request);
        return Result.success();
    }

    @Operation(summary = "删除SKU")
    @DeleteMapping("/skus/{skuId}")
    public Result<Void> deleteSku(Authentication authentication, @PathVariable Long skuId) {
        Long userId = (Long) authentication.getPrincipal();
        Merchant merchant = merchantMapper.findByUserId(userId);
        productService.deleteSku(merchant.getId(), skuId);
        return Result.success();
    }
}
