package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Banner;
import org.example.teasystem.service.BannerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Banner控制器（用户端）
 */
@Tag(name = "轮播图接口")
@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "获取首页轮播图列表")
    @GetMapping
    public Result<List<Banner>> getEnabledBanners() {
        return Result.success(bannerService.getEnabledBanners());
    }
}
