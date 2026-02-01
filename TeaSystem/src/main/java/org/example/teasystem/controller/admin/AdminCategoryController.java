package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Category;
import org.example.teasystem.mapper.CategoryMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类管理控制器（管理端）
 */
@Tag(name = "管理端-分类管理")
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;

    @Operation(summary = "获取所有分类")
    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryMapper.findAll());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        return Result.success(categoryMapper.findById(id));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        return Result.success(category.getId());
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        category.setId(id);
        categoryMapper.update(category);
        return Result.success();
    }

    @Operation(summary = "启用分类")
    @PostMapping("/{id}/enable")
    public Result<Void> enableCategory(@PathVariable Long id) {
        categoryMapper.updateStatus(id, 1);
        return Result.success();
    }

    @Operation(summary = "禁用分类")
    @PostMapping("/{id}/disable")
    public Result<Void> disableCategory(@PathVariable Long id) {
        categoryMapper.updateStatus(id, 0);
        return Result.success();
    }
}
