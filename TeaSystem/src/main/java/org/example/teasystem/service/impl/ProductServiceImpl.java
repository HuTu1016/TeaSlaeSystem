package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.product.*;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.ProductSku;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.ProductSkuMapper;
import org.example.teasystem.service.ProductCacheService;
import org.example.teasystem.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private ProductCacheService productCacheService;

    @Override
    public PageResult<ProductListVO> getProductList(ProductQueryRequest request) {
        // 使用缓存服务获取商品列表
        return productCacheService.getProductListCached(request);
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        ProductDetailVO detailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, detailVO);

        // 查询SKU列表
        List<ProductSku> skuList = productSkuMapper.selectByProductId(productId);
        List<ProductDetailVO.SkuVO> skuVOList = skuList.stream()
                .map(sku -> {
                    ProductDetailVO.SkuVO skuVO = new ProductDetailVO.SkuVO();
                    skuVO.setId(sku.getId());
                    skuVO.setSkuName(sku.getSkuName());
                    skuVO.setPrice(sku.getPrice());
                    skuVO.setStock(sku.getStock());
                    skuVO.setStatus(sku.getStatus());
                    return skuVO;
                })
                .collect(Collectors.toList());
        detailVO.setSkus(skuVOList);

        // 设置图片列表（使用mainImage）
        List<String> images = new ArrayList<>();
        if (product.getMainImage() != null && !product.getMainImage().isEmpty()) {
            images.add(product.getMainImage());
        }
        detailVO.setImages(images);

        return detailVO;
    }

    @Override
    @Transactional
    public Long createProduct(Long merchantId, ProductSaveRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setMerchantId(merchantId);
        product.setStatus("DRAFT"); // 默认草稿
        product.setSalesCount(0);
        product.setViewCount(0);

        // 使用第一张图片作为主图
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setMainImage(request.getImages().get(0));
        }

        productMapper.insert(product);

        // 创建SKU
        if (request.getSkus() != null) {
            for (ProductSaveRequest.SkuSaveRequest skuRequest : request.getSkus()) {
                ProductSku sku = new ProductSku();
                sku.setProductId(product.getId());
                sku.setSkuName(skuRequest.getSkuName());
                sku.setPrice(skuRequest.getPrice());
                sku.setStock(skuRequest.getStock());
                sku.setStatus(skuRequest.getStatus() != null ? skuRequest.getStatus() : 1);
                productSkuMapper.insert(sku);
            }
        }

        return product.getId();
    }

    @Override
    @Transactional
    public void updateProduct(Long merchantId, Long productId, ProductSaveRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权修改该商品");
        }

        BeanUtils.copyProperties(request, product);

        // 使用第一张图片作为主图
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setMainImage(request.getImages().get(0));
        }

        productMapper.update(product);

        // 清除商品缓存
        productCacheService.evictAllProductCache();
    }

    @Override
    @Transactional
    public void onShelf(Long merchantId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该商品");
        }

        // 商品上架需要平台审核，状态改为PENDING（待审核）
        productMapper.updateStatus(productId, "PENDING");
    }

    @Override
    @Transactional
    public void offShelf(Long merchantId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该商品");
        }

        productMapper.updateStatus(productId, "OFF_SHELF");

        // 清除商品缓存
        productCacheService.evictAllProductCache();
    }

    @Override
    public List<ProductListVO> getHotProducts(int limit) {
        // 使用缓存服务获取热门商品
        return productCacheService.getHotProductsCached(limit);
    }

    @Override
    public List<ProductListVO> getNewProducts(int limit) {
        // 使用缓存服务获取新品
        return productCacheService.getNewProductsCached(limit);
    }

    @Override
    public List<ProductListVO> getSimilarProducts(Long productId, int limit) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return new ArrayList<>();
        }

        // 根据分类查询相似商品
        List<Product> products = productMapper.selectSimilar(product.getCategoryId(), productId, limit);

        return products.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为商品列表VO
     */
    private ProductListVO convertToListVO(Product product) {
        ProductListVO vo = new ProductListVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setSubtitle(product.getSubtitle());
        vo.setOrigin(product.getOrigin());
        vo.setProcess(product.getProcess());
        vo.setTaste(product.getTaste());
        vo.setMainImage(product.getMainImage());
        vo.setMinPrice(product.getMinPrice());
        vo.setMaxPrice(product.getMaxPrice());
        vo.setSalesCount(product.getSalesCount());
        vo.setStatus(product.getStatus());
        return vo;
    }

    @Override
    public PageResult<ProductListVO> getMerchantProducts(Long merchantId, String status, String keyword, Integer page,
            Integer pageSize) {
        int offset = (page - 1) * pageSize;

        // 使用 productMapper.findPage 方法，传入 merchantId
        List<Product> products = productMapper.findPage(
                merchantId,
                null, // categoryId
                status,
                null, // origin
                null, // process
                null, // minPrice
                null, // maxPrice
                keyword,
                "newest", // sort
                offset,
                pageSize);

        long total = productMapper.count(
                merchantId,
                null, // categoryId
                status,
                null, // origin
                null, // process
                null, // minPrice
                null, // maxPrice
                keyword);

        List<ProductListVO> voList = products.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, voList);
    }

    @Override
    public ProductDetailVO getMerchantProductDetail(Long merchantId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权查看该商品");
        }

        ProductDetailVO detailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, detailVO);

        // 查询SKU列表
        List<ProductSku> skuList = productSkuMapper.selectByProductId(productId);
        List<ProductDetailVO.SkuVO> skuVOList = skuList.stream()
                .map(sku -> {
                    ProductDetailVO.SkuVO skuVO = new ProductDetailVO.SkuVO();
                    skuVO.setId(sku.getId());
                    skuVO.setSkuName(sku.getSkuName());
                    skuVO.setPrice(sku.getPrice());
                    skuVO.setStock(sku.getStock());
                    skuVO.setStatus(sku.getStatus());
                    return skuVO;
                })
                .collect(Collectors.toList());
        detailVO.setSkus(skuVOList);

        // 设置图片列表
        List<String> images = new ArrayList<>();
        if (product.getMainImage() != null && !product.getMainImage().isEmpty()) {
            images.add(product.getMainImage());
        }
        detailVO.setImages(images);

        return detailVO;
    }

    @Override
    @Transactional
    public Long createSku(Long merchantId, Long productId, ProductSaveRequest.SkuSaveRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该商品");
        }

        ProductSku sku = new ProductSku();
        sku.setProductId(productId);
        sku.setSkuName(request.getSkuName());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        sku.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        productSkuMapper.insert(sku);

        // 更新商品价格区间
        updateProductPriceRange(productId);

        return sku.getId();
    }

    @Override
    @Transactional
    public void updateSku(Long merchantId, Long skuId, ProductSaveRequest.SkuSaveRequest request) {
        ProductSku sku = productSkuMapper.findById(skuId);
        if (sku == null) {
            throw new BusinessException(404, "SKU不存在");
        }

        Product product = productMapper.selectById(sku.getProductId());
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该SKU");
        }

        sku.setSkuName(request.getSkuName());
        sku.setPrice(request.getPrice());
        sku.setStock(request.getStock());
        if (request.getStatus() != null) {
            sku.setStatus(request.getStatus());
        }
        productSkuMapper.update(sku);

        // 更新商品价格区间
        updateProductPriceRange(sku.getProductId());
    }

    @Override
    @Transactional
    public void deleteSku(Long merchantId, Long skuId) {
        ProductSku sku = productSkuMapper.findById(skuId);
        if (sku == null) {
            throw new BusinessException(404, "SKU不存在");
        }

        Product product = productMapper.selectById(sku.getProductId());
        if (product == null || !product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该SKU");
        }

        productSkuMapper.deleteById(skuId);

        // 更新商品价格区间
        updateProductPriceRange(sku.getProductId());
    }

    /**
     * 更新商品价格区间（根据SKU最低/最高价格）
     */
    private void updateProductPriceRange(Long productId) {
        List<ProductSku> skuList = productSkuMapper.findByProductId(productId);
        if (skuList.isEmpty()) {
            productMapper.updatePriceRange(productId, null, null);
            return;
        }

        java.math.BigDecimal minPrice = skuList.stream()
                .map(ProductSku::getPrice)
                .min(java.math.BigDecimal::compareTo)
                .orElse(null);
        java.math.BigDecimal maxPrice = skuList.stream()
                .map(ProductSku::getPrice)
                .max(java.math.BigDecimal::compareTo)
                .orElse(null);

        productMapper.updatePriceRange(productId, minPrice, maxPrice);
    }
}
