package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.Coupon;
import org.example.teasystem.entity.MembershipLevel;
import org.example.teasystem.entity.UserCoupon;
import org.example.teasystem.security.SecurityUtils;
import org.example.teasystem.service.CouponService;
import org.example.teasystem.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券和会员控制器（用户端）
 * 
 * <p>
 * 提供优惠券领取、查询以及会员信息查询等功能
 * </p>
 * 
 * <h3>优惠券使用流程：</h3>
 * <ol>
 * <li>调用 GET /api/coupons/available 获取可领取的优惠券列表</li>
 * <li>调用 POST /api/coupons/{couponId}/receive 领取优惠券</li>
 * <li>调用 GET /api/my/coupons 查看我的优惠券</li>
 * <li>下单时调用 GET /api/my/coupons/available?amount=xxx 获取可用于该订单金额的优惠券</li>
 * </ol>
 */
@Tag(name = "优惠券与会员接口", description = "用户端优惠券领取、查询及会员信息管理接口")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final MemberService memberService;

    // ==================== 优惠券 ====================

    /**
     * 获取可领取的优惠券列表
     * 
     * <p>
     * 返回所有状态为ACTIVE、库存未领完、且未过期的优惠券
     * </p>
     */
    @Operation(summary = "获取可领取的优惠券列表", description = "返回所有可供用户领取的优惠券。筛选条件：状态为ACTIVE、已领取数量<总数量、未过期。" +
            "用户可以在领券中心展示这些优惠券供领取。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @GetMapping("/coupons/available")
    public Result<List<Coupon>> getAvailableCoupons() {
        return Result.success(couponService.getAvailableCoupons());
    }

    /**
     * 领取优惠券
     * 
     * <p>
     * 用户领取指定的优惠券，领取后会生成一张用户优惠券记录
     * </p>
     */
    @Operation(summary = "领取优惠券", description = "用户领取指定ID的优惠券。领取成功后会在user_coupon表生成一条记录。\n\n" +
            "**领取限制：**\n" +
            "- 每人限领数量（limit_per_user）\n" +
            "- 优惠券库存（total_count - received_count > 0）\n" +
            "- 优惠券必须处于ACTIVE状态\n\n" +
            "**有效期计算：**\n" +
            "- 如果优惠券设置了valid_days，则从领取时间开始计算N天有效\n" +
            "- 如果优惠券设置了valid_from/valid_to，则使用固定时间段")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "领取成功"),
            @ApiResponse(responseCode = "400", description = "领取失败（超过限领数量/库存不足/已下架）", content = @Content),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @PostMapping("/coupons/{couponId}/receive")
    public Result<Void> receiveCoupon(
            @Parameter(description = "优惠券ID", required = true, example = "1") @PathVariable Long couponId) {
        Long userId = SecurityUtils.getCurrentUserId();
        couponService.receiveCoupon(userId, couponId);
        return Result.success();
    }

    /**
     * 获取我的优惠券
     * 
     * <p>
     * 查询当前用户已领取的优惠券列表，可按状态筛选
     * </p>
     */
    @Operation(summary = "获取我的优惠券", description = "查询当前登录用户已领取的所有优惠券。可通过status参数筛选状态。\n\n" +
            "**status可选值：**\n" +
            "- 不传或空：返回所有优惠券\n" +
            "- UNUSED：未使用\n" +
            "- USED：已使用\n" +
            "- EXPIRED：已过期\n\n" +
            "**返回数据说明：**\n" +
            "- 包含优惠券详情（name, type, amount等）\n" +
            "- 包含用户优惠券信息（valid_from, valid_to, status等）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @GetMapping("/my/coupons")
    public Result<List<UserCoupon>> getMyCoupons(
            @Parameter(description = "优惠券状态：UNUSED-未使用, USED-已使用, EXPIRED-已过期。不传则返回全部", example = "UNUSED") @RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(couponService.getUserCoupons(userId, status));
    }

    /**
     * 获取可用于指定金额的优惠券
     * 
     * <p>
     * 下单时调用，筛选出满足使用门槛且未过期的优惠券
     * </p>
     */
    @Operation(summary = "获取可用于指定金额的优惠券", description = "根据订单金额筛选可用的优惠券。常用于下单页面展示可选优惠券。\n\n" +
            "**筛选条件：**\n" +
            "- 状态为UNUSED\n" +
            "- 未过期（valid_to > 当前时间）\n" +
            "- 满足使用门槛（订单金额 >= min_amount）\n\n" +
            "**使用示例：**\n" +
            "用户订单金额为150元，调用 /api/my/coupons/available?amount=150，" +
            "返回所有min_amount <= 150的可用优惠券")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @GetMapping("/my/coupons/available")
    public Result<List<UserCoupon>> getAvailableCouponsForAmount(
            @Parameter(description = "订单金额，用于筛选满足门槛的优惠券", required = true, example = "150.00") @RequestParam BigDecimal amount) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(couponService.getAvailableCouponsForAmount(userId, amount));
    }

    // ==================== 会员 ====================

    /**
     * 获取我的会员信息
     * 
     * <p>
     * 返回当前用户的会员等级和积分信息
     * </p>
     */
    @Operation(summary = "获取我的会员信息", description = "获取当前登录用户的会员等级和积分信息。\n\n" +
            "**会员等级说明：**\n" +
            "- 等级根据累计消费金额自动升级\n" +
            "- 不同等级享有不同折扣优惠\n\n" +
            "**积分说明：**\n" +
            "- 消费可获得积分\n" +
            "- 积分可用于兑换商品或抵扣")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @GetMapping("/my/membership")
    public Result<MembershipVO> getMyMembership() {
        Long userId = SecurityUtils.getCurrentUserId();
        MembershipLevel level = memberService.getUserLevel(userId);
        Integer points = memberService.getUserPoints(userId);

        MembershipVO vo = new MembershipVO();
        vo.setLevel(level);
        vo.setPoints(points);
        return Result.success(vo);
    }

    /**
     * 计算会员折扣
     * 
     * <p>
     * 根据会员等级计算指定金额的折扣后价格
     * </p>
     */
    @Operation(summary = "计算会员折扣", description = "根据当前用户的会员等级，计算指定金额的折扣后价格。\n\n" +
            "**使用场景：**\n" +
            "- 下单前预览会员折扣价\n" +
            "- 购物车页面展示会员优惠\n\n" +
            "**计算规则：**\n" +
            "- 根据会员等级对应的折扣比例计算\n" +
            "- 返回折扣后的最终金额")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "计算成功，返回折扣后金额"),
            @ApiResponse(responseCode = "401", description = "未登录", content = @Content)
    })
    @GetMapping("/my/member-discount")
    public Result<BigDecimal> calculateMemberDiscount(
            @Parameter(description = "原始订单金额", required = true, example = "200.00") @RequestParam BigDecimal amount) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(memberService.calculateMemberDiscount(userId, amount));
    }

    /**
     * 会员信息VO
     */
    @Schema(description = "会员信息")
    @lombok.Data
    public static class MembershipVO {
        @Schema(description = "会员等级信息")
        private MembershipLevel level;

        @Schema(description = "当前积分", example = "1500")
        private Integer points;
    }
}
