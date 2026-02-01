package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Coupon;
import org.example.teasystem.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {

    /**
     * 获取可领取的优惠券列表
     */
    List<Coupon> getAvailableCoupons();

    /**
     * 领取优惠券
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 获取用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(Long userId, String status);

    /**
     * 获取用户可用于指定金额的优惠券
     */
    List<UserCoupon> getAvailableCouponsForAmount(Long userId, BigDecimal amount);

    /**
     * 使用优惠券
     */
    void useCoupon(Long userCouponId, Long orderId);

    /**
     * 计算优惠金额
     */
    BigDecimal calculateDiscount(Long couponId, BigDecimal orderAmount);

    // ==================== 管理端 ====================

    /**
     * 获取优惠券列表（管理端）
     */
    PageResult<Coupon> getCouponList(String status, Integer page, Integer pageSize);

    /**
     * 创建优惠券
     */
    Long createCoupon(Coupon coupon);

    /**
     * 更新优惠券
     */
    void updateCoupon(Coupon coupon);

    /**
     * 启用/停用优惠券
     */
    void updateCouponStatus(Long id, String status);
}
