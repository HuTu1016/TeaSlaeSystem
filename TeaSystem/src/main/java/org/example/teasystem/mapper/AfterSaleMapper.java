package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.AfterSale;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 售后Mapper
 */
@Mapper
public interface AfterSaleMapper {

        /**
         * 根据ID查询售后
         */
        @Select("SELECT * FROM after_sale WHERE id = #{id}")
        AfterSale selectById(@Param("id") Long id);

        /**
         * 根据售后单号查询
         */
        @Select("SELECT * FROM after_sale WHERE after_sale_no = #{afterSaleNo}")
        AfterSale findByAfterSaleNo(@Param("afterSaleNo") String afterSaleNo);

        /**
         * 根据订单项ID查询未完结的售后
         */
        @Select("SELECT * FROM after_sale WHERE order_item_id = #{orderItemId} " +
                        "AND status NOT IN ('COMPLETED', 'CANCELLED') LIMIT 1")
        AfterSale findPendingByOrderItemId(@Param("orderItemId") Long orderItemId);

        /**
         * 根据订单项ID查询售后
         */
        @Select("SELECT * FROM after_sale WHERE order_item_id = #{orderItemId}")
        AfterSale selectByOrderItemId(@Param("orderItemId") Long orderItemId);

        /**
         * 分页查询用户售后
         */
        @Select("<script>" +
                        "SELECT * FROM after_sale WHERE user_id = #{userId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<AfterSale> selectByUserId(@Param("userId") Long userId, @Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计用户售后数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM after_sale WHERE user_id = #{userId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        long countByUserId(@Param("userId") Long userId, @Param("status") String status);

        /**
         * 分页查询商家售后
         */
        @Select("<script>" +
                        "SELECT * FROM after_sale WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<AfterSale> selectByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计商家售后数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM after_sale WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        long countByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status);

        /**
         * 插入售后
         */
        @Insert("INSERT INTO after_sale (after_sale_no, order_id, order_item_id, user_id, merchant_id, " +
                        "type, status, reason, description, images, apply_amount, created_at) VALUES " +
                        "(#{afterSaleNo}, #{orderId}, #{orderItemId}, #{userId}, #{merchantId}, #{type}, " +
                        "#{status}, #{reason}, #{description}, #{images}, #{applyAmount}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(AfterSale afterSale);

        /**
         * 更新售后状态
         */
        @Update("UPDATE after_sale SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 更新售后状态并添加商家备注
         */
        @Update("UPDATE after_sale SET status = #{status}, merchant_comment = #{comment}, updated_at = NOW() WHERE id = #{id}")
        int updateStatusWithComment(@Param("id") Long id, @Param("status") String status,
                        @Param("comment") String comment);

        /**
         * 完成售后
         */
        @Update("UPDATE after_sale SET status = 'COMPLETED', completed_at = #{completedAt}, updated_at = NOW() WHERE id = #{id}")
        int updateCompleted(@Param("id") Long id, @Param("completedAt") LocalDateTime completedAt);

        /**
         * 更新售后记录
         */
        @Update("<script>" +
                        "UPDATE after_sale SET updated_at = NOW() " +
                        "<if test='status != null'>, status = #{status}</if>" +
                        "<if test='merchantComment != null'>, merchant_comment = #{merchantComment}</if>" +
                        " WHERE id = #{id}" +
                        "</script>")
        int update(AfterSale afterSale);

        // ==================== 管理端方法 ====================

        /**
         * 管理端分页查询售后列表
         */
        @Select("<script>" +
                        "SELECT * FROM after_sale WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='type != null'>AND type = #{type}</if>" +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='userId != null'>AND user_id = #{userId}</if>" +
                        "<if test='afterSaleNo != null and afterSaleNo != \"\"'>AND after_sale_no LIKE CONCAT('%',#{afterSaleNo},'%')</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<AfterSale> selectByAdmin(@Param("status") String status, @Param("type") String type,
                        @Param("merchantId") Long merchantId, @Param("userId") Long userId,
                        @Param("afterSaleNo") String afterSaleNo,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 管理端统计售后数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM after_sale WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='type != null'>AND type = #{type}</if>" +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='userId != null'>AND user_id = #{userId}</if>" +
                        "<if test='afterSaleNo != null and afterSaleNo != \"\"'>AND after_sale_no LIKE CONCAT('%',#{afterSaleNo},'%')</if>" +
                        "</script>")
        long countByAdmin(@Param("status") String status, @Param("type") String type,
                        @Param("merchantId") Long merchantId, @Param("userId") Long userId,
                        @Param("afterSaleNo") String afterSaleNo);

        /**
         * 统计全部售后数量
         */
        @Select("SELECT COUNT(*) FROM after_sale")
        Long countAll();

        /**
         * 统计今日售后数量
         */
        @Select("SELECT COUNT(*) FROM after_sale WHERE DATE(created_at) = CURDATE()")
        Long countToday();

        /**
         * 管理端介入售后
         */
        @Update("UPDATE after_sale SET status = #{status}, admin_comment = #{adminComment}, updated_at = NOW() WHERE id = #{id}")
        int adminIntervene(@Param("id") Long id, @Param("status") String status, @Param("adminComment") String adminComment);
}
