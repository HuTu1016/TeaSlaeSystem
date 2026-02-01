package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Review;
import org.example.teasystem.entity.ReviewReply;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface ReviewMapper {

        /**
         * 根据ID查询评论
         */
        @Select("SELECT * FROM review WHERE id = #{id}")
        Review findById(@Param("id") Long id);

        /**
         * 根据订单项ID查询评论
         */
        @Select("SELECT * FROM review WHERE order_item_id = #{orderItemId}")
        Review findByOrderItemId(@Param("orderItemId") Long orderItemId);

        /**
         * 分页查询商品评论（仅已审核通过）
         */
        @Select("SELECT * FROM review WHERE product_id = #{productId} AND status = 'APPROVED' " +
                        "ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
        List<Review> findByProductId(@Param("productId") Long productId,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计商品已审核评论数量
         */
        @Select("SELECT COUNT(*) FROM review WHERE product_id = #{productId} AND status = 'APPROVED'")
        long countApprovedByProductId(@Param("productId") Long productId);

        /**
         * 分页查询待审核评论
         */
        @Select("<script>" +
                        "SELECT * FROM review WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Review> findByStatus(@Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计待审核评论数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM review WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        long countByStatus(@Param("status") String status);

        /**
         * 插入评论
         */
        @Insert("INSERT INTO review (product_id, order_item_id, user_id, rating, content, images, status, created_at) "
                        +
                        "VALUES (#{productId}, #{orderItemId}, #{userId}, #{rating}, #{content}, #{images}, #{status}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Review review);

        /**
         * 更新评论状态
         */
        @Update("UPDATE review SET status = #{status}, reject_reason = #{rejectReason} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status,
                        @Param("rejectReason") String rejectReason);

        // ==================== 评论回复 ====================

        /**
         * 根据评论ID查询所有回复（支持多级回复）
         */
        @Select("SELECT * FROM review_reply WHERE review_id = #{reviewId} ORDER BY created_at ASC")
        List<ReviewReply> findRepliesByReviewId(@Param("reviewId") Long reviewId);

        /**
         * 插入回复
         */
        @Insert("INSERT INTO review_reply (review_id, merchant_id, content, created_at) " +
                        "VALUES (#{reviewId}, #{merchantId}, #{content}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertReply(ReviewReply reply);

        // ==================== 新增方法 ====================

        /**
         * 根据ID查询评论（别名）
         */
        @Select("SELECT * FROM review WHERE id = #{id}")
        Review selectById(@Param("id") Long id);

        /**
         * 根据订单和商品查询评论
         */
        /**
         * 根据订单和商品查询评论
         */
        @Select("SELECT r.* FROM review r INNER JOIN order_item oi ON r.order_item_id = oi.id WHERE oi.order_id = #{orderId} AND r.product_id = #{productId}")
        Review selectByOrderAndProduct(@Param("orderId") Long orderId, @Param("productId") Long productId);

        /**
         * 分页查询商品评论
         */
        @Select("<script>" +
                        "SELECT * FROM review WHERE product_id = #{productId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Review> selectByProductId(@Param("productId") Long productId, @Param("status") Integer status,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计商品评论数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM review WHERE product_id = #{productId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        int countByProductId(@Param("productId") Long productId, @Param("status") Integer status);

        /**
         * 查询待审核评论
         */
        @Select("SELECT * FROM review WHERE status = 0 ORDER BY created_at DESC LIMIT #{offset}, #{size}")
        List<Review> selectPending(@Param("offset") int offset, @Param("size") int size);

        /**
         * 统计待审核评论数量
         */
        @Select("SELECT COUNT(*) FROM review WHERE status = 0")
        int countPending();

        /**
         * 管理员查询评论列表
         */
        @Select("<script>" +
                        "SELECT * FROM review WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='productId != null'>AND product_id = #{productId}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Review> selectByAdmin(@Param("status") Integer status, @Param("productId") Long productId,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 管理员统计评论数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM review WHERE 1=1 " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='productId != null'>AND product_id = #{productId}</if>" +
                        "</script>")
        int countByAdmin(@Param("status") Integer status, @Param("productId") Long productId);

        /**
         * 删除评论
         */
        @Delete("DELETE FROM review WHERE id = #{id}")
        int delete(@Param("id") Long id);

        /**
         * 查询用户评论
         */
        @Select("SELECT * FROM review WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{size}")
        List<Review> selectByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计用户评论数量
         */
        @Select("SELECT COUNT(*) FROM review WHERE user_id = #{userId}")
        int countByUserId(@Param("userId") Long userId);

        /**
         * 商家查询所有商品评论列表
         */
        @Select("<script>" +
                        "SELECT r.* FROM review r " +
                        "INNER JOIN product p ON r.product_id = p.id " +
                        "WHERE p.merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND r.status = #{status}</if>" +
                        "<if test='hasReply != null and hasReply == true'>" +
                        "AND EXISTS (SELECT 1 FROM review_reply rr WHERE rr.review_id = r.id)" +
                        "</if>" +
                        "<if test='hasReply != null and hasReply == false'>" +
                        "AND NOT EXISTS (SELECT 1 FROM review_reply rr WHERE rr.review_id = r.id)" +
                        "</if>" +
                        " ORDER BY r.created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Review> selectByMerchant(@Param("merchantId") Long merchantId,
                        @Param("status") String status,
                        @Param("hasReply") Boolean hasReply,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 商家统计所有商品评论数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM review r " +
                        "INNER JOIN product p ON r.product_id = p.id " +
                        "WHERE p.merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND r.status = #{status}</if>" +
                        "<if test='hasReply != null and hasReply == true'>" +
                        "AND EXISTS (SELECT 1 FROM review_reply rr WHERE rr.review_id = r.id)" +
                        "</if>" +
                        "<if test='hasReply != null and hasReply == false'>" +
                        "AND NOT EXISTS (SELECT 1 FROM review_reply rr WHERE rr.review_id = r.id)" +
                        "</if>" +
                        "</script>")
        int countByMerchant(@Param("merchantId") Long merchantId,
                        @Param("status") String status,
                        @Param("hasReply") Boolean hasReply);

        /**
         * 统计商家待回复评论数量
         */
        @Select("SELECT COUNT(*) FROM review r " +
                        "INNER JOIN product p ON r.product_id = p.id " +
                        "WHERE p.merchant_id = #{merchantId} " +
                        "AND r.status = 'APPROVED' " +
                        "AND NOT EXISTS (SELECT 1 FROM review_reply rr WHERE rr.review_id = r.id)")
        Long countPendingReplyByMerchant(@Param("merchantId") Long merchantId);
}
