package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Product;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.UserEventMapper;
import org.example.teasystem.service.SearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现（MySQL版，默认）
 */
@Slf4j
@Service("mysqlSearchService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "search.use-elasticsearch", havingValue = "false", matchIfMissing = true)
public class SearchServiceImpl implements SearchService {

    private final ProductMapper productMapper;
    private final UserEventMapper userEventMapper;

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId, String origin, String process,
            BigDecimal minPrice, BigDecimal maxPrice, String sort,
            Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        // 构建排序条件（MVP：数据库排序）
        String orderBy = buildOrderBy(sort);

        // 使用MySQL like查询（MVP）
        // 增强：可替换为ES查询
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
        // 增强: 使用ES completion suggest
        return productMapper.selectTitleSuggestions(keyword.trim(), limit);
    }

    @Override
    public List<HotKeywordVO> getHotKeywords(Integer limit) {
        // 从数据库查询热词
        List<String> keywords = new ArrayList<>();
        try {
            keywords = userEventMapper.selectHotKeywordsOnly(limit);
        } catch (Exception e) {
            // user_event表可能不存在或结构不匹配，使用备选方案
        }

        // 如果没有热搜词，使用热销商品标题作为备选
        if (keywords.isEmpty()) {
            try {
                keywords = productMapper.selectHotProductTitles(limit);
            } catch (Exception e) {
                // 静默失败
            }
        }

        return keywords.stream()
                .map(kw -> new HotKeywordVO(kw, 0L))
                .collect(Collectors.toList());
    }

    /**
     * 构建排序条件
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
                return "sales_count DESC"; // MVP: relevance降级为销量排序
        }
    }
}
