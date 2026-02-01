package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Coupon;
import org.example.teasystem.entity.UserCoupon;
import org.example.teasystem.mapper.CouponMapper;
import org.example.teasystem.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    @Override
    public List<Coupon> getAvailableCoupons() {
        return couponMapper.selectAvailable(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException(404, "优惠券不存在");
        }
        if (!"ACTIVE".equals(coupon.getStatus())) {
            throw new BusinessException(400, "优惠券不可用");
        }
        if (coupon.getReceivedCount() >= coupon.getTotalCount()) {
            throw new BusinessException(400, "优惠券已领完");
        }

        // 检查领取限制
        int userReceived = couponMapper.countUserCoupon(userId, couponId);
        if (userReceived >= coupon.getLimitPerUser()) {
            throw new BusinessException(400, "已达到领取上限");
        }

        // 计算有效期
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validTo;
        if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            validTo = validFrom.plusDays(coupon.getValidDays());
        } else {
            validTo = coupon.getValidTo();
        }

        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setValidFrom(validFrom);
        userCoupon.setValidTo(validTo);
        couponMapper.insertUserCoupon(userCoupon);

        // 更新已领取数量
        couponMapper.incrementReceived(couponId);
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId, String status) {
        return couponMapper.selectUserCoupons(userId, status);
    }

    @Override
    public List<UserCoupon> getAvailableCouponsForAmount(Long userId, BigDecimal amount) {
        List<UserCoupon> coupons = couponMapper.selectUserAvailableCoupons(userId);
        // 过滤满足使用门槛的优惠券
        return coupons.stream()
                .filter(uc -> {
                    Coupon coupon = couponMapper.selectById(uc.getCouponId());
                    return coupon != null &&
                            (coupon.getMinAmount() == null || amount.compareTo(coupon.getMinAmount()) >= 0);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = couponMapper.selectUserCouponById(userCouponId);
        if (userCoupon == null) {
            throw new BusinessException(404, "优惠券不存在");
        }
        if (!"UNUSED".equals(userCoupon.getStatus())) {
            throw new BusinessException(400, "优惠券不可用");
        }
        if (userCoupon.getValidTo().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "优惠券已过期");
        }
        couponMapper.useUserCoupon(userCouponId, orderId);
    }

    @Override
    public BigDecimal calculateDiscount(Long couponId, BigDecimal orderAmount) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        // 检查使用门槛
        if (coupon.getMinAmount() != null && orderAmount.compareTo(coupon.getMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if ("AMOUNT".equals(coupon.getType())) {
            // 满减券
            discount = coupon.getAmount();
        } else {
            // 折扣券
            BigDecimal discountRate = BigDecimal.valueOf(100 - coupon.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            discount = orderAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

            // 最大抵扣限制
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        }

        // 不能超过订单金额
        return discount.min(orderAmount);
    }

    @Override
    public PageResult<Coupon> getCouponList(String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Coupon> list = couponMapper.selectList(status, offset, pageSize);
        long total = couponMapper.count(status);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public Long createCoupon(Coupon coupon) {
        coupon.setReceivedCount(0);
        coupon.setStatus("ACTIVE");
        couponMapper.insert(coupon);
        return coupon.getId();
    }

    @Override
    public void updateCoupon(Coupon coupon) {
        Coupon existing = couponMapper.selectById(coupon.getId());
        if (existing == null) {
            throw new BusinessException(404, "优惠券不存在");
        }
        couponMapper.update(coupon);
    }

    @Override
    public void updateCouponStatus(Long id, String status) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException(404, "优惠券不存在");
        }
        couponMapper.updateStatus(id, status);
    }
}
