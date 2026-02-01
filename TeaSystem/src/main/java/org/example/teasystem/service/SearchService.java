package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 搜索商品
     * 
     * @param keyword    关键词
     * @param categoryId 类目ID
     * @param origin     产地
     * @param process    工艺
     * @param minPrice   最低价
     * @param maxPrice   最高价
     * @param sort       排序方式：relevance, sales_desc, price_asc, price_desc, newest
     * @param page       页码
     * @param pageSize   每页数量
     */
    PageResult<Product> searchProducts(String keyword, Long categoryId, String origin, String process,
            BigDecimal minPrice, BigDecimal maxPrice, String sort,
            Integer page, Integer pageSize);

    /**
     * 获取搜索建议（自动补全）
     */
    List<String> getSuggestions(String keyword, Integer limit);

    /**
     * 获取热搜词
     */
    List<HotKeywordVO> getHotKeywords(Integer limit);

    /**
     * 热搜词VO
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    class HotKeywordVO {
        private String keyword;
        private Long count;
    }
}
