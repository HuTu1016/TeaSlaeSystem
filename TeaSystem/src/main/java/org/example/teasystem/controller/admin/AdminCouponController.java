package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Coupon;
import org.example.teasystem.service.CouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券管理控制器（管理端）
 */
@Tag(name = "管理端-优惠券管理")
@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    private final CouponService couponService;

    @Operation(summary = "获取优惠券列表")
    @GetMapping
    public Result<PageResult<Coupon>> getList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(couponService.getCouponList(status, page, pageSize));
    }

    @Operation(summary = "创建优惠券")
    @PostMapping
    public Result<Long> createCoupon(@RequestBody Coupon coupon) {
        Long id = couponService.createCoupon(coupon);
        return Result.success(id);
    }

    @Operation(summary = "更新优惠券")
    @PutMapping("/{id}")
    public Result<Void> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        couponService.updateCoupon(coupon);
        return Result.success();
    }

    @Operation(summary = "启用优惠券")
    @PostMapping("/{id}/enable")
    public Result<Void> enableCoupon(@PathVariable Long id) {
        couponService.updateCouponStatus(id, "ACTIVE");
        return Result.success();
    }

    @Operation(summary = "停用优惠券")
    @PostMapping("/{id}/disable")
    public Result<Void> disableCoupon(@PathVariable Long id) {
        couponService.updateCouponStatus(id, "INACTIVE");
        return Result.success();
    }
}
