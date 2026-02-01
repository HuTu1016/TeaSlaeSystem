package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.product.ProductListVO;
import org.example.teasystem.dto.product.ProductQueryRequest;

import java.util.List;

/**
 * 商品缓存服务接口
 * 负责热点商品数据的 Redis 缓存管理
 */
public interface ProductCacheService {

    /**
     * 获取缓存的热门商品（缓存24小时）
     */
    List<ProductListVO> getHotProductsCached(int limit);

    /**
     * 获取缓存的新品商品（缓存24小时）
     */
    List<ProductListVO> getNewProductsCached(int limit);

    /**
     * 获取缓存的商品列表（缓存1小时）
     */
    PageResult<ProductListVO> getProductListCached(ProductQueryRequest request);

    /**
     * 清除热门商品缓存
     */
    void evictHotProductsCache();

    /**
     * 清除新品缓存
     */
    void evictNewProductsCache();

    /**
     * 清除商品列表缓存
     */
    void evictProductListCache();

    /**
     * 清除所有商品相关缓存
     */
    void evictAllProductCache();
}
