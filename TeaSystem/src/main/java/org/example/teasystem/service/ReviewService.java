package org.example.teasystem.service;

import org.example.teasystem.dto.request.ReviewCreateRequest;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.PendingReviewItemVO;
import org.example.teasystem.dto.response.ReviewVO;

import java.util.List;

/**
 * 评论服务接口
 */
public interface ReviewService {

    /**
     * 创建评论
     */
    void createReview(Long userId, ReviewCreateRequest request);

    /**
     * 获取用户评论列表
     */
    PageResponse<ReviewVO> getUserReviews(Long userId, Integer page, Integer pageSize);

    /**
     * 获取待评价商品列表
     */
    List<PendingReviewItemVO> getPendingReviewItems(Long userId);

    /**
     * 获取已全部评价的订单ID列表
     */
    List<Long> getFullyReviewedOrderIds(Long userId);

    /**
     * 获取商品评论列表
     */
    PageResponse<ReviewVO> getProductReviews(Long productId, Integer page, Integer pageSize);

    /**
     * 管理员审核评论
     */
    void auditReview(Long reviewId, String status, String rejectReason);

    /**
     * 商家回复评论
     */
    void replyReview(Long merchantId, Long reviewId, String content);

    /**
     * 获取商家评论列表
     */
    PageResponse<ReviewVO> getMerchantReviews(Long merchantId, String status, Boolean hasReply,
            Integer page, Integer pageSize);

    /**
     * 管理员获取评论列表
     */
    PageResponse<ReviewVO> getAdminReviews(String status, Long productId, Integer page, Integer pageSize);

    /**
     * 获取待审核评论列表（管理员）
     */
    PageResponse<ReviewVO> getPendingReviews(Integer page, Integer pageSize);

    /**
     * 审核评论（使用审核请求对象）
     */
    void auditReview(Long reviewId, org.example.teasystem.dto.request.ReviewAuditRequest request);

    /**
     * 按状态获取评论列表（管理员）
     */
    PageResponse<ReviewVO> getReviewsByAdmin(Integer status, Long productId, Integer page, Integer pageSize);

    /**
     * 删除评论
     */
    void deleteReview(Long reviewId);
}
