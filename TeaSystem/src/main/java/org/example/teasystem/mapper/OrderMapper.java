package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Order;
import org.example.teasystem.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper {

        /**
         * 根据ID查询订单
         */
        @Select("SELECT * FROM `order` WHERE id = #{id}")
        Order selectById(@Param("id") Long id);

        /**
         * 根据订单号查询订单
         */
        @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
        Order findByOrderNo(@Param("orderNo") String orderNo);

        /**
         * 分页查询用户订单
         */
        @Select("<script>" +
                        "SELECT * FROM `order` WHERE user_id = #{userId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Order> selectByUserId(@Param("userId") Long userId, @Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计用户订单数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM `order` WHERE user_id = #{userId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        long countByUserId(@Param("userId") Long userId, @Param("status") String status);

        /**
         * 分页查询商家订单
         */
        @Select("<script>" +
                        "SELECT * FROM `order` WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Order> selectByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计商家订单数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM `order` WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        long countByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status);

        /**
         * 插入订单
         */
        @Insert("INSERT INTO `order` (order_no, user_id, merchant_id, status, total_amount, pay_amount, " +
                        "coupon_id, coupon_discount_amount, pay_status, address_snapshot, remark, created_at) " +
                        "VALUES (#{orderNo}, #{userId}, #{merchantId}, #{status}, #{totalAmount}, #{payAmount}, " +
                        "#{couponId}, #{couponDiscountAmount}, #{payStatus}, #{addressSnapshot}, #{remark}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Order order);

        /**
         * 更新订单状态
         */
        @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 更新支付状态
         */
        @Update("UPDATE `order` SET pay_status = #{payStatus}, status = #{status}, paid_at = #{paidAt} WHERE id = #{id}")
        int updatePayStatus(@Param("id") Long id, @Param("payStatus") String payStatus,
                        @Param("status") String status, @Param("paidAt") LocalDateTime paidAt);

        /**
         * 更新发货状态
         */
        @Update("UPDATE `order` SET status = 'SHIPPED', shipped_at = #{shippedAt} WHERE id = #{id}")
        int updateShipped(@Param("id") Long id, @Param("shippedAt") LocalDateTime shippedAt);

        /**
         * 更新完成状态
         */
        @Update("UPDATE `order` SET status = 'COMPLETED', completed_at = #{completedAt} WHERE id = #{id}")
        int updateCompleted(@Param("id") Long id, @Param("completedAt") LocalDateTime completedAt);

        /**
         * 更新取消状态
         */
        @Update("UPDATE `order` SET status = 'CANCELLED', cancelled_at = #{cancelledAt} WHERE id = #{id}")
        int updateCancelled(@Param("id") Long id, @Param("cancelledAt") LocalDateTime cancelledAt);

        /**
         * 查询超时未支付订单
         */
        @Select("SELECT * FROM `order` WHERE status = 'CREATED' AND created_at < #{expireTime}")
        List<Order> findExpiredOrders(@Param("expireTime") LocalDateTime expireTime);

        /**
         * 查询热门商品标题
         */
        @Select("SELECT title FROM product WHERE status = 'ON_SHELF' ORDER BY sales_count DESC LIMIT #{limit}")
        List<String> selectHotProductTitles(@Param("limit") int limit);

        /**
         * 更新发货信息
         */
        @Update("UPDATE `order` SET tracking_no = #{trackingNo}, tracking_company = #{trackingCompany}, " +
                        "shipped_at = NOW(), updated_at = NOW() WHERE id = #{id}")
        int updateShipment(@Param("id") Long id, @Param("trackingNo") String trackingNo,
                        @Param("trackingCompany") String trackingCompany);

        /**
         * 更新支付信息
         */
        @Update("UPDATE `order` SET payment_method = #{paymentMethod}, payment_no = #{paymentNo}, " +
                        "paid_at = #{paidAt}, updated_at = NOW() WHERE id = #{id}")
        int updatePaymentInfo(@Param("id") Long id, @Param("paymentMethod") String paymentMethod,
                        @Param("paymentNo") String paymentNo, @Param("paidAt") LocalDateTime paidAt);

        // ==================== 订单项 ====================

        /**
         * 根据订单ID查询订单项
         */
        @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
        List<OrderItem> selectOrderItems(@Param("orderId") Long orderId);

        /**
         * 分页查询用户已支付订单（用于支出明细）
         */
        @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND pay_status = 'PAID' " +
                        "ORDER BY paid_at DESC LIMIT #{offset}, #{pageSize}")
        List<Order> selectPaidOrdersByUserId(@Param("userId") Long userId,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计用户已支付订单数量
         */
        @Select("SELECT COUNT(*) FROM `order` WHERE user_id = #{userId} AND pay_status = 'PAID'")
        long countPaidOrdersByUserId(@Param("userId") Long userId);

        /**
         * 根据ID查询订单项
         */
        @Select("SELECT * FROM order_item WHERE id = #{id}")
        OrderItem selectOrderItemById(@Param("id") Long id);

        /**
         * 插入订单项
         */
        @Insert("INSERT INTO order_item (order_id, product_id, sku_id, title_snapshot, sku_name_snapshot, " +
                        "image_snapshot, price, quantity, subtotal, created_at) VALUES (#{orderId}, #{productId}, " +
                        "#{skuId}, #{titleSnapshot}, #{skuNameSnapshot}, #{imageSnapshot}, #{price}, #{quantity}, #{subtotal}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertOrderItem(OrderItem item);

        // ==================== 销售额统计 ====================

        /**
         * 统计商家总销售额（已支付订单）
         */
        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` WHERE merchant_id = #{merchantId} AND pay_status = 'PAID'")
        java.math.BigDecimal sumSalesByMerchantId(@Param("merchantId") Long merchantId);

        /**
         * 统计商家今日销售额
         */
        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` WHERE merchant_id = #{merchantId} AND pay_status = 'PAID' AND DATE(paid_at) = CURDATE()")
        java.math.BigDecimal sumTodaySalesByMerchantId(@Param("merchantId") Long merchantId);

        /**
         * 统计商家今日订单数
         */
        @Select("SELECT COUNT(*) FROM `order` WHERE merchant_id = #{merchantId} AND DATE(created_at) = CURDATE()")
        Long countTodayByMerchantId(@Param("merchantId") Long merchantId);

        /**
         * 按日期统计商家销售趋势
         */
        @Select("SELECT DATE(paid_at) as date, COUNT(*) as orderCount, COALESCE(SUM(pay_amount), 0) as salesAmount " +
                        "FROM `order` WHERE merchant_id = #{merchantId} AND pay_status = 'PAID' " +
                        "AND paid_at >= #{startDate} AND paid_at < #{endDate} " +
                        "GROUP BY DATE(paid_at) ORDER BY date")
        @Results({
                        @Result(property = "date", column = "date"),
                        @Result(property = "orderCount", column = "orderCount"),
                        @Result(property = "salesAmount", column = "salesAmount")
        })
        List<java.util.Map<String, Object>> selectDailySalesByMerchantId(@Param("merchantId") Long merchantId,
                        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        // ==================== 平台统计（管理端） ====================

        /**
         * 统计平台全部订单数
         */
        @Select("SELECT COUNT(*) FROM `order`")
        Long countAll();

        /**
         * 统计平台今日订单数
         */
        @Select("SELECT COUNT(*) FROM `order` WHERE DATE(created_at) = CURDATE()")
        Long countToday();

        /**
         * 统计平台总销售额（已支付订单）
         */
        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` WHERE pay_status = 'PAID'")
        java.math.BigDecimal sumAllSales();

        /**
         * 统计平台今日销售额
         */
        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` WHERE pay_status = 'PAID' AND DATE(paid_at) = CURDATE()")
        java.math.BigDecimal sumTodaySales();

        /**
         * 按日期统计平台销售趋势
         */
        @Select("SELECT DATE(paid_at) as date, COUNT(*) as orderCount, COALESCE(SUM(pay_amount), 0) as salesAmount " +
                        "FROM `order` WHERE pay_status = 'PAID' " +
                        "AND paid_at >= #{startDate} AND paid_at < #{endDate} " +
                        "GROUP BY DATE(paid_at) ORDER BY date")
        List<java.util.Map<String, Object>> selectDailySales(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * 管理端分页查询订单（支持多条件筛选）
         */
        @Select("<script>" +
                        "SELECT * FROM `order` WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='payStatus != null'>AND pay_status = #{payStatus}</if>" +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='userId != null'>AND user_id = #{userId}</if>" +
                        "<if test='orderNo != null and orderNo != \"\"'>AND order_no LIKE CONCAT('%',#{orderNo},'%')</if>" +
                        "<if test='startDate != null'>AND created_at &gt;= #{startDate}</if>" +
                        "<if test='endDate != null'>AND created_at &lt; #{endDate}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Order> selectByAdmin(@Param("status") String status, @Param("payStatus") String payStatus,
                        @Param("merchantId") Long merchantId, @Param("userId") Long userId,
                        @Param("orderNo") String orderNo, @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate, @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        /**
         * 管理端统计订单数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM `order` WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='payStatus != null'>AND pay_status = #{payStatus}</if>" +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='userId != null'>AND user_id = #{userId}</if>" +
                        "<if test='orderNo != null and orderNo != \"\"'>AND order_no LIKE CONCAT('%',#{orderNo},'%')</if>" +
                        "<if test='startDate != null'>AND created_at &gt;= #{startDate}</if>" +
                        "<if test='endDate != null'>AND created_at &lt; #{endDate}</if>" +
                        "</script>")
        long countByAdmin(@Param("status") String status, @Param("payStatus") String payStatus,
                        @Param("merchantId") Long merchantId, @Param("userId") Long userId,
                        @Param("orderNo") String orderNo, @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
