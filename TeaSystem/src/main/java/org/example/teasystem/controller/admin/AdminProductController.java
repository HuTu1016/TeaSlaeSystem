package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.mapper.CategoryMapper;
import org.example.teasystem.entity.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端商品管理控制器
 */
@Tag(name = "管理端-商品管理")
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductMapper productMapper;
    private final MerchantMapper merchantMapper;
    private final CategoryMapper categoryMapper;

    @Operation(summary = "获取商品列表")
    @GetMapping
    public Result<PageResult<ProductVO>> getProductList(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        int offset = (page - 1) * pageSize;
        List<Product> products = productMapper.findPage(merchantId, categoryId, status, null, null, 
                minPrice, maxPrice, keyword, null, offset, pageSize);
        long total = productMapper.count(merchantId, categoryId, status, null, null, minPrice, maxPrice, keyword);
        
        List<ProductVO> voList = products.stream().map(this::convertToVO).collect(Collectors.toList());
        
        PageResult<ProductVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{productId}")
    public Result<ProductVO> getProductDetail(@PathVariable Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(convertToVO(product));
    }

    @Operation(summary = "上架商品")
    @PostMapping("/{productId}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        productMapper.updateStatus(productId, "ON_SHELF");
        return Result.success();
    }

    @Operation(summary = "下架商品")
    @PostMapping("/{productId}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long productId, @RequestParam(required = false) String reason) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        productMapper.updateStatus(productId, "OFF_SHELF");
        return Result.success();
    }

    @Operation(summary = "删除商品（逻辑删除）")
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        productMapper.updateStatus(productId, "DELETED");
        return Result.success();
    }

    @Operation(summary = "获取商品统计")
    @GetMapping("/stats")
    public Result<ProductStatsVO> getProductStats() {
        ProductStatsVO stats = new ProductStatsVO();
        stats.setTotalProducts(productMapper.count(null, null, null, null, null, null, null, null));
        stats.setOnShelfProducts(productMapper.count(null, null, "ON_SHELF", null, null, null, null, null));
        stats.setOffShelfProducts(productMapper.count(null, null, "OFF_SHELF", null, null, null, null, null));
        stats.setPendingProducts(productMapper.countPending());
        return Result.success(stats);
    }

    // ==================== 商品审核 ====================

    @Operation(summary = "获取待审核商品列表")
    @GetMapping("/pending")
    public Result<PageResult<ProductVO>> getPendingList(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        int offset = (page - 1) * pageSize;
        List<Product> products = productMapper.selectPendingPage(merchantId, keyword, offset, pageSize);
        long total = productMapper.countPendingByCondition(merchantId, keyword);
        
        List<ProductVO> voList = products.stream().map(this::convertToVO).collect(Collectors.toList());
        
        PageResult<ProductVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return Result.success(result);
    }

    @Operation(summary = "审核通过商品")
    @PostMapping("/{productId}/approve")
    public Result<Void> approveProduct(@PathVariable Long productId,
            @RequestParam(required = false) String comment,
            @RequestAttribute("userId") Long userId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        if (!"PENDING".equals(product.getStatus())) {
            return Result.fail("该商品不是待审核状态");
        }
        productMapper.audit(productId, "OFF_SHELF", comment, userId);
        return Result.success();
    }

    @Operation(summary = "审核拒绝商品")
    @PostMapping("/{productId}/reject")
    public Result<Void> rejectProduct(@PathVariable Long productId,
            @RequestParam String reason,
            @RequestAttribute("userId") Long userId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        if (!"PENDING".equals(product.getStatus())) {
            return Result.fail("该商品不是待审核状态");
        }
        productMapper.audit(productId, "REJECTED", reason, userId);
        return Result.success();
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        
        // 获取商家信息
        if (product.getMerchantId() != null) {
            Merchant merchant = merchantMapper.findById(product.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getShopName());
            }
        }
        
        // 获取分类信息
        if (product.getCategoryId() != null) {
            Category category = categoryMapper.findById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        return vo;
    }

    /**
     * 商品VO
     */
    @lombok.Data
    public static class ProductVO {
        private Long id;
        private Long merchantId;
        private String merchantName;
        private Long categoryId;
        private String categoryName;
        private String title;
        private String subtitle;
        private String origin;
        private String process;
        private String mainImage;
        private String status;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Integer salesCount;
        private Integer viewCount;
        private java.time.LocalDateTime createdAt;
    }

    /**
     * 商品统计VO
     */
    @lombok.Data
    public static class ProductStatsVO {
        private Long totalProducts;
        private Long onShelfProducts;
        private Long offShelfProducts;
        private Long pendingProducts;
    }
}
