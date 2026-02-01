package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Category;
import org.example.teasystem.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类控制器
 */
@Tag(name = "分类接口")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryMapper categoryMapper;
    
    @Operation(summary = "获取分类树")
    @GetMapping
    public Result<List<CategoryTreeVO>> getCategoryTree() {
        List<Category> allCategories = categoryMapper.findAllEnabled();
        List<CategoryTreeVO> tree = buildTree(allCategories, 0L);
        return Result.success(tree);
    }
    
    /**
     * 构建分类树
     */
    private List<CategoryTreeVO> buildTree(List<Category> categories, Long parentId) {
        return categories.stream()
                .filter(c -> c.getParentId().equals(parentId))
                .map(c -> {
                    CategoryTreeVO vo = new CategoryTreeVO();
                    vo.setId(c.getId());
                    vo.setName(c.getName());
                    vo.setIcon(c.getIcon());
                    vo.setChildren(buildTree(categories, c.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }
    
    @lombok.Data
    public static class CategoryTreeVO {
        private Long id;
        private String name;
        private String icon;
        private List<CategoryTreeVO> children;
    }
}
