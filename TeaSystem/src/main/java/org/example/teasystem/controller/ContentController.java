package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ContentArticle;
import org.example.teasystem.service.ContentService;
import org.springframework.web.bind.annotation.*;

/**
 * 内容文章控制器（用户端）
 */
@Tag(name = "内容文章接口")
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "获取茶文化文章列表")
    @GetMapping("/culture")
    public Result<PageResult<ContentArticle>> getCultureList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(contentService.getPublishedList("CULTURE", page, pageSize));
    }

    @Operation(summary = "获取冲泡教程列表")
    @GetMapping("/tutorials")
    public Result<PageResult<ContentArticle>> getTutorialList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(contentService.getPublishedList("BREW_TUTORIAL", page, pageSize));
    }

    @Operation(summary = "获取所有内容列表")
    @GetMapping("/list")
    public Result<PageResult<ContentArticle>> getList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(contentService.getPublishedList(type, page, pageSize));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<ContentArticle> getDetail(@PathVariable Long id) {
        return Result.success(contentService.getDetail(id));
    }
}
