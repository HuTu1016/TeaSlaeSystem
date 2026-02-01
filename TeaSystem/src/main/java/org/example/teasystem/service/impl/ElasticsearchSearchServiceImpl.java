package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.document.ProductDocument;
import org.example.teasystem.entity.Product;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.UserEventMapper;
import org.example.teasystem.repository.ProductSearchRepository;
import org.example.teasystem.service.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索服务实现（ES + Redis增强版）
 */
@Slf4j
@Service("esSearchService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "search.use-elasticsearch", havingValue = "true")
public class ElasticsearchSearchServiceImpl implements SearchService {

    private final ProductMapper productMapper;
    private final UserEventMapper userEventMapper;
    private final ProductSearchRepository productSearchRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final StringRedisTemplate redisTemplate;

    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";
    private static final int HOT_KEYWORDS_CACHE_MINUTES = 5;

    @Value("${search.use-elasticsearch:false}")
    private boolean useElasticsearch;

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId, String origin, String process,
            BigDecimal minPrice, BigDecimal maxPrice, String sort,
            Integer page, Integer pageSize) {
        // 根据配置决定使用ES还是MySQL
        if (useElasticsearch) {
            return searchFromElasticsearch(keyword, categoryId, origin, process, minPrice, maxPrice, sort, page,
                    pageSize);
        } else {
            return searchFromMySQL(keyword, categoryId, origin, process, minPrice, maxPrice, sort, page, pageSize);
        }
    }

    /**
     * ES搜索
     */
    private PageResult<Product> searchFromElasticsearch(String keyword, Long categoryId, String origin, String process,
            BigDecimal minPrice, BigDecimal maxPrice, String sort,
            Integer page, Integer pageSize) {
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 必须是上架状态
            boolQuery.must(QueryBuilders.termQuery("status", "ON_SHELF"));

            // 关键词搜索（多字段匹配）
            if (keyword != null && !keyword.isEmpty()) {
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "title", "subtitle", "origin", "taste")
                        .boost(2.0f));
            }

            // 类目过滤
            if (categoryId != null) {
                boolQuery.filter(QueryBuilders.termQuery("categoryId", categoryId));
            }

            // 产地过滤
            if (origin != null && !origin.isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("origin", origin));
            }

            // 工艺过滤
            if (process != null && !process.isEmpty()) {
                boolQuery.filter(QueryBuilders.termQuery("process", process));
            }

            // 价格区间
            if (minPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("minPrice").gte(minPrice.doubleValue()));
            }
            if (maxPrice != null) {
                boolQuery.filter(QueryBuilders.rangeQuery("maxPrice").lte(maxPrice.doubleValue()));
            }

            // 构建查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(page - 1, pageSize));

            // 排序
            addSort(queryBuilder, sort);

            NativeSearchQuery searchQuery = queryBuilder.build();
            SearchHits<ProductDocument> searchHits = elasticsearchRestTemplate.search(searchQuery,
                    ProductDocument.class);

            // 转换结果
            List<Product> products = searchHits.stream()
                    .map(hit -> convertToProduct(hit.getContent()))
                    .collect(Collectors.toList());

            return PageResult.of(page, pageSize, searchHits.getTotalHits(), products);

        } catch (Exception e) {
            log.error("ES搜索异常，降级到MySQL", e);
            return searchFromMySQL(keyword, categoryId, origin, process, minPrice, maxPrice, sort, page, pageSize);
        }
    }

    /**
     * MySQL搜索（降级方案）
     */
    private PageResult<Product> searchFromMySQL(String keyword, Long categoryId, String origin, String process,
            BigDecimal minPrice, BigDecimal maxPrice, String sort,
            Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        String orderBy = buildOrderBy(sort);

        List<Product> products = productMapper.searchProducts(
                keyword, categoryId, origin, process, minPrice, maxPrice, orderBy, offset, pageSize);
        long total = productMapper.countSearchProducts(keyword, categoryId, origin, process, minPrice, maxPrice);

        return PageResult.of(page, pageSize, total, products);
    }

    @Override
    public List<String> getSuggestions(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // MVP: 从商品标题中提取前缀匹配
        return productMapper.selectTitleSuggestions(keyword.trim(), limit);
    }

    @Override
    public List<HotKeywordVO> getHotKeywords(Integer limit) {
        // 尝试从Redis缓存获取
        try {
            String cachedData = redisTemplate.opsForValue().get(HOT_KEYWORDS_KEY);
            if (cachedData != null && !cachedData.isEmpty()) {
                // 简化处理：缓存的是逗号分隔的关键词
                String[] keywords = cachedData.split(",");
                List<HotKeywordVO> result = new ArrayList<>();
                for (String kw : keywords) {
                    if (!kw.isEmpty() && result.size() < limit) {
                        result.add(new HotKeywordVO(kw.trim(), 0L));
                    }
                }
                if (!result.isEmpty()) {
                    return result;
                }
            }
        } catch (Exception e) {
            log.warn("从Redis获取热词缓存失败", e);
        }

        // 从数据库查询热搜词
        List<String> keywords = new ArrayList<>();
        try {
            keywords = userEventMapper.selectHotKeywordsOnly(limit);
        } catch (Exception e) {
            log.warn("从user_event表查询热词失败，使用商品标题作为备选", e);
        }

        // 如果没有热搜词，使用热销商品标题作为备选
        if (keywords.isEmpty()) {
            try {
                keywords = productMapper.selectHotProductTitles(limit);
            } catch (Exception e) {
                log.warn("获取热销商品标题失败", e);
            }
        }

        // 缓存到Redis
        try {
            if (!keywords.isEmpty()) {
                String cacheValue = String.join(",", keywords);
                redisTemplate.opsForValue().set(HOT_KEYWORDS_KEY, cacheValue, HOT_KEYWORDS_CACHE_MINUTES,
                        TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.warn("缓存热词到Redis失败", e);
        }

        return keywords.stream()
                .map(kw -> new HotKeywordVO(kw, 0L))
                .collect(Collectors.toList());
    }

    /**
     * 添加排序
     */
    private void addSort(NativeSearchQueryBuilder queryBuilder, String sort) {
        if (sort == null) {
            queryBuilder.withSort(SortBuilders.fieldSort("salesCount").order(SortOrder.DESC));
            return;
        }
        switch (sort) {
            case "sales_desc":
                queryBuilder.withSort(SortBuilders.fieldSort("salesCount").order(SortOrder.DESC));
                break;
            case "price_asc":
                queryBuilder.withSort(SortBuilders.fieldSort("minPrice").order(SortOrder.ASC));
                break;
            case "price_desc":
                queryBuilder.withSort(SortBuilders.fieldSort("minPrice").order(SortOrder.DESC));
                break;
            case "newest":
                queryBuilder.withSort(SortBuilders.fieldSort("createdAt").order(SortOrder.DESC));
                break;
            case "relevance":
            default:
                queryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
                break;
        }
    }

    /**
     * MySQL排序
     */
    private String buildOrderBy(String sort) {
        if (sort == null) {
            return "sales_count DESC";
        }
        switch (sort) {
            case "sales_desc":
                return "sales_count DESC";
            case "price_asc":
                return "min_price ASC";
            case "price_desc":
                return "min_price DESC";
            case "newest":
                return "created_at DESC";
            case "relevance":
            default:
                return "sales_count DESC";
        }
    }

    /**
     * ES文档转Product
     */
    private Product convertToProduct(ProductDocument doc) {
        Product product = new Product();
        product.setId(doc.getId());
        product.setTitle(doc.getTitle());
        product.setSubtitle(doc.getSubtitle());
        product.setCategoryId(doc.getCategoryId());
        product.setOrigin(doc.getOrigin());
        product.setProcess(doc.getProcess());
        product.setTaste(doc.getTaste());
        product.setMerchantId(doc.getMerchantId());
        product.setMinPrice(doc.getMinPrice());
        product.setMaxPrice(doc.getMaxPrice());
        product.setSalesCount(doc.getSalesCount());
        product.setMainImage(doc.getMainImage());
        product.setStatus(doc.getStatus());
        product.setCreatedAt(doc.getCreatedAt());
        return product;
    }
}
