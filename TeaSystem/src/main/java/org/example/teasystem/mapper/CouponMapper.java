package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Coupon;
import org.example.teasystem.entity.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券Mapper
 */
@Mapper
public interface CouponMapper {

        /**
         * 根据ID查询优惠券
         */
        @Select("SELECT * FROM coupon WHERE id = #{id}")
        Coupon selectById(@Param("id") Long id);

        /**
         * 查询可领取的优惠券列表
         */
        @Select("SELECT * FROM coupon WHERE status = 'ACTIVE' " +
                        "AND received_count < total_count " +
                        "AND (valid_to IS NULL OR valid_to > #{now}) " +
                        "ORDER BY created_at DESC")
        List<Coupon> selectAvailable(LocalDateTime now);

        /**
         * 分页查询优惠券（管理端）
         */
        @Select("<script>" +
                        "SELECT * FROM coupon WHERE 1=1 " +
                        "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
                        " ORDER BY id DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Coupon> selectList(@Param("status") String status, @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        /**
         * 统计优惠券数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM coupon WHERE 1=1 " +
                        "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
                        "</script>")
        long count(@Param("status") String status);

        /**
         * 插入优惠券
         */
        @Insert("INSERT INTO coupon (name, type, amount, discount_percent, min_amount, max_discount, " +
                        "total_count, received_count, limit_per_user, valid_days, valid_from, valid_to, status, created_at, updated_at) "
                        +
                        "VALUES (#{name}, #{type}, #{amount}, #{discountPercent}, #{minAmount}, #{maxDiscount}, " +
                        "#{totalCount}, 0, #{limitPerUser}, #{validDays}, #{validFrom}, #{validTo}, #{status}, NOW(), NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Coupon coupon);

        /**
         * 更新优惠券
         */
        @Update("UPDATE coupon SET name = #{name}, type = #{type}, amount = #{amount}, " +
                        "discount_percent = #{discountPercent}, min_amount = #{minAmount}, max_discount = #{maxDiscount}, "
                        +
                        "total_count = #{totalCount}, limit_per_user = #{limitPerUser}, " +
                        "valid_days = #{validDays}, valid_from = #{validFrom}, valid_to = #{validTo}, updated_at = NOW() WHERE id = #{id}")
        int update(Coupon coupon);

        /**
         * 更新状态
         */
        @Update("UPDATE coupon SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 增加已领取数量
         */
        @Update("UPDATE coupon SET received_count = received_count + 1 WHERE id = #{id}")
        int incrementReceived(@Param("id") Long id);

        // ==================== 用户优惠券 ====================

        /**
         * 根据ID查询用户优惠券
         */
        @Select("SELECT * FROM user_coupon WHERE id = #{id}")
        UserCoupon selectUserCouponById(@Param("id") Long id);

        /**
         * 查询用户持有的优惠券
         */
        @Select("<script>" +
                        "SELECT uc.*, c.name, c.type, c.amount, c.discount_percent, c.min_amount, c.max_discount " +
                        "FROM user_coupon uc JOIN coupon c ON uc.coupon_id = c.id " +
                        "WHERE uc.user_id = #{userId} " +
                        "<if test='status != null and status != \"\"'>AND uc.status = #{status}</if>" +
                        " ORDER BY uc.created_at DESC" +
                        "</script>")
        List<UserCoupon> selectUserCoupons(@Param("userId") Long userId, @Param("status") String status);

        /**
         * 查询用户可用的优惠券（未使用且未过期）
         */
        @Select("SELECT uc.*, c.name, c.type, c.amount, c.discount_percent, c.min_amount, c.max_discount " +
                        "FROM user_coupon uc JOIN coupon c ON uc.coupon_id = c.id " +
                        "WHERE uc.user_id = #{userId} AND uc.status = 'UNUSED' AND uc.valid_to > NOW() " +
                        "ORDER BY uc.valid_to ASC")
        List<UserCoupon> selectUserAvailableCoupons(@Param("userId") Long userId);

        /**
         * 统计用户已领取某优惠券的数量
         */
        @Select("SELECT COUNT(*) FROM user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId}")
        int countUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

        /**
         * 插入用户优惠券
         */
        @Insert("INSERT INTO user_coupon (user_id, coupon_id, status, valid_from, valid_to, created_at) " +
                        "VALUES (#{userId}, #{couponId}, 'UNUSED', #{validFrom}, #{validTo}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertUserCoupon(UserCoupon userCoupon);

        /**
         * 使用优惠券
         */
        @Update("UPDATE user_coupon SET status = 'USED', used_at = NOW(), used_order_id = #{orderId} WHERE id = #{id}")
        int useUserCoupon(@Param("id") Long id, @Param("orderId") Long orderId);

        /**
         * 标记过期优惠券
         */
        @Update("UPDATE user_coupon SET status = 'EXPIRED' WHERE status = 'UNUSED' AND valid_to < NOW()")
        int markExpiredCoupons();

        /**
         * 恢复优惠券（用于订单取消或退款时退回优惠券）
         * 仅当优惠券未过期时才恢复为UNUSED状态
         */
        @Update("UPDATE user_coupon SET status = 'UNUSED', used_at = NULL, used_order_id = NULL " +
                        "WHERE id = #{id} AND status = 'USED' AND valid_to > NOW()")
        int restoreUserCoupon(@Param("id") Long id);

        /**
         * 根据订单ID查询使用的用户优惠券
         */
        @Select("SELECT * FROM user_coupon WHERE used_order_id = #{orderId}")
        UserCoupon selectUserCouponByOrderId(@Param("orderId") Long orderId);
}
