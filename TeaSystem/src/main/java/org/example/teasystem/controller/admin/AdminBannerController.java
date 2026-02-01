package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Banner;
import org.example.teasystem.service.BannerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Banner管理控制器（管理端）
 */
@Tag(name = "管理端-轮播图管理")
@RestController
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final BannerService bannerService;

    @Operation(summary = "获取轮播图列表")
    @GetMapping
    public Result<PageResult<Banner>> getList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(bannerService.getBannerList(status, page, pageSize));
    }

    @Operation(summary = "创建轮播图")
    @PostMapping
    public Result<Long> create(@RequestBody Banner banner) {
        Long id = bannerService.createBanner(banner);
        return Result.success(id);
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.updateBanner(banner);
        return Result.success();
    }

    @Operation(summary = "启用轮播图")
    @PostMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        bannerService.enableBanner(id);
        return Result.success();
    }

    @Operation(summary = "禁用轮播图")
    @PostMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        bannerService.disableBanner(id);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.success();
    }
}
