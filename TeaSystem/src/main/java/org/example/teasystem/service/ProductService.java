package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.product.ProductDetailVO;
import org.example.teasystem.dto.product.ProductListVO;
import org.example.teasystem.dto.product.ProductQueryRequest;
import org.example.teasystem.dto.product.ProductSaveRequest;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 分页查询商品列表
     */
    PageResult<ProductListVO> getProductList(ProductQueryRequest request);

    /**
     * 获取商品详情
     */
    ProductDetailVO getProductDetail(Long productId);

    /**
     * 商家创建商品
     */
    Long createProduct(Long merchantId, ProductSaveRequest request);

    /**
     * 商家更新商品
     */
    void updateProduct(Long merchantId, Long productId, ProductSaveRequest request);

    /**
     * 商品上架
     */
    void onShelf(Long merchantId, Long productId);

    /**
     * 商品下架
     */
    void offShelf(Long merchantId, Long productId);

    /**
     * 获取热门商品
     */
    List<ProductListVO> getHotProducts(int limit);

    /**
     * 获取新品
     */
    List<ProductListVO> getNewProducts(int limit);

    /**
     * 获取相似商品推荐
     */
    List<ProductListVO> getSimilarProducts(Long productId, int limit);

    /**
     * 商家获取自己的商品列表
     */
    PageResult<ProductListVO> getMerchantProducts(Long merchantId, String status, String keyword, Integer page,
            Integer pageSize);

    /**
     * 商家获取商品详情（用于编辑回显）
     */
    ProductDetailVO getMerchantProductDetail(Long merchantId, Long productId);

    /**
     * 创建SKU
     */
    Long createSku(Long merchantId, Long productId, ProductSaveRequest.SkuSaveRequest request);

    /**
     * 更新SKU
     */
    void updateSku(Long merchantId, Long skuId, ProductSaveRequest.SkuSaveRequest request);

    /**
     * 删除SKU
     */
    void deleteSku(Long merchantId, Long skuId);
}
