package org.example.teasystem.repository;

import org.example.teasystem.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品ES仓库
 */
@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    /**
     * 根据状态查询
     */
    List<ProductDocument> findByStatus(String status);

    /**
     * 根据类目查询
     */
    List<ProductDocument> findByCategoryIdAndStatus(Long categoryId, String status);
}
