package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.document.ProductDocument;
import org.example.teasystem.entity.Product;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.repository.ProductSearchRepository;
import org.example.teasystem.service.ProductIndexService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品索引服务实现
 * 仅在search.use-elasticsearch=true时启用
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "search.use-elasticsearch", havingValue = "true")
public class ProductIndexServiceImpl implements ProductIndexService {

    private final ProductMapper productMapper;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public void indexProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            indexProduct(product);
        }
    }

    @Override
    public void indexProducts(List<Long> productIds) {
        for (Long productId : productIds) {
            try {
                indexProduct(productId);
            } catch (Exception e) {
                log.error("索引商品失败: productId={}", productId, e);
            }
        }
    }

    @Override
    public void deleteProductIndex(Long productId) {
        try {
            productSearchRepository.deleteById(productId);
            log.info("删除商品索引: productId={}", productId);
        } catch (Exception e) {
            log.error("删除商品索引失败: productId={}", productId, e);
        }
    }

    @Override
    public void reindexAll() {
        log.info("开始全量重建商品索引...");
        long startTime = System.currentTimeMillis();

        // 分批查询所有上架商品
        int page = 0;
        int pageSize = 100;
        int total = 0;

        while (true) {
            List<Product> products = productMapper.findPage(
                    null, null, "ON_SHELF", null, null, null, null, null, null,
                    page * pageSize, pageSize);

            if (products.isEmpty()) {
                break;
            }

            // 批量索引
            List<ProductDocument> docs = new ArrayList<>();
            for (Product product : products) {
                docs.add(convertToDocument(product));
            }
            productSearchRepository.saveAll(docs);

            total += products.size();
            page++;
            log.info("已索引 {} 条商品", total);
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("全量索引完成，共 {} 条商品，耗时 {} ms", total, cost);
    }

    @Override
    public void indexProduct(Product product) {
        try {
            ProductDocument doc = convertToDocument(product);
            productSearchRepository.save(doc);
            log.debug("索引商品: productId={}", product.getId());
        } catch (Exception e) {
            log.error("索引商品失败: productId={}", product.getId(), e);
        }
    }

    /**
     * 将Product转换为ES文档
     */
    private ProductDocument convertToDocument(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId());
        doc.setTitle(product.getTitle());
        doc.setSubtitle(product.getSubtitle());
        doc.setCategoryId(product.getCategoryId());
        doc.setOrigin(product.getOrigin());
        doc.setProcess(product.getProcess());
        doc.setTaste(product.getTaste());
        doc.setMerchantId(product.getMerchantId());
        doc.setMinPrice(product.getMinPrice());
        doc.setMaxPrice(product.getMaxPrice());
        doc.setSalesCount(product.getSalesCount());
        doc.setMainImage(product.getMainImage());
        doc.setStatus(product.getStatus());
        doc.setInStock(true); // 简化处理，实际需查询SKU
        doc.setCreatedAt(product.getCreatedAt());
        doc.setUpdatedAt(LocalDateTime.now());
        doc.setSuggest(product.getTitle()); // 用于自动补全
        return doc;
    }
}
