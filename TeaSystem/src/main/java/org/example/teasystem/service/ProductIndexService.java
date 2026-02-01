package org.example.teasystem.service;

import org.example.teasystem.entity.Product;

import java.util.List;

/**
 * 商品索引服务接口
 */
public interface ProductIndexService {

    /**
     * 索引单个商品
     */
    void indexProduct(Long productId);

    /**
     * 批量索引商品
     */
    void indexProducts(List<Long> productIds);

    /**
     * 删除商品索引
     */
    void deleteProductIndex(Long productId);

    /**
     * 全量重建索引
     */
    void reindexAll();

    /**
     * 将Product转换为ES文档并索引
     */
    void indexProduct(Product product);
}
