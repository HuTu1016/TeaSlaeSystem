package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Product;
import org.example.teasystem.service.SearchService;
import org.example.teasystem.service.SearchService.HotKeywordVO;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索控制器
 */
@Tag(name = "搜索接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索商品")
    @GetMapping("/products")
    public Result<PageResult<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String process,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        // 限制pageSize
        if (pageSize > 50) {
            pageSize = 50;
        }

        return Result.success(searchService.searchProducts(
                keyword, categoryId, origin, process, minPrice, maxPrice, sort, page, pageSize));
    }

    @Operation(summary = "获取搜索建议")
    @GetMapping("/suggest")
    public Result<List<String>> getSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {

        if (limit > 20) {
            limit = 20;
        }

        return Result.success(searchService.getSuggestions(keyword, limit));
    }

    @Operation(summary = "获取热搜词")
    @GetMapping("/hot-words")
    public Result<List<HotKeywordVO>> getHotWords(
            @RequestParam(defaultValue = "10") Integer limit) {

        if (limit > 50) {
            limit = 50;
        }

        return Result.success(searchService.getHotKeywords(limit));
    }
}
