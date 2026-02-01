package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ContentArticle;
import org.example.teasystem.service.ContentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 内容文章管理控制器（管理端）
 */
@Tag(name = "管理端-内容管理")
@RestController
@RequestMapping("/admin/content")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminContentController {

    private final ContentService contentService;

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<ContentArticle> getDetail(@PathVariable Long id) {
        return Result.success(contentService.getArticleById(id));
    }

    @Operation(summary = "获取文章列表")
    @GetMapping("/list")
    public Result<PageResult<ContentArticle>> getList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(contentService.getList(type, status, page, pageSize));
    }

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<Long> createArticle(@RequestBody ContentArticle article) {
        Long id = contentService.createArticle(article);
        return Result.success(id);
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @RequestBody ContentArticle article) {
        article.setId(id);
        contentService.updateArticle(article);
        return Result.success();
    }

    @Operation(summary = "发布文章")
    @PostMapping("/{id}/publish")
    public Result<Void> publishArticle(@PathVariable Long id) {
        contentService.publishArticle(id);
        return Result.success();
    }

    @Operation(summary = "下架文章")
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublishArticle(@PathVariable Long id) {
        contentService.unpublishArticle(id);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        contentService.deleteArticle(id);
        return Result.success();
    }
}
