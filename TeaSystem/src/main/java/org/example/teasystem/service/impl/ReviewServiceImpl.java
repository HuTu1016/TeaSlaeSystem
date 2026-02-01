package org.example.teasystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.dto.request.ReviewCreateRequest;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.PendingReviewItemVO;
import org.example.teasystem.dto.response.ReviewVO;
import org.example.teasystem.entity.*;
import org.example.teasystem.mapper.*;
import org.example.teasystem.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final MerchantMapper merchantMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void createReview(Long userId, ReviewCreateRequest request) {
        // 1. 验证订单是否属于该用户
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40400, "订单不存在");
        }

        // 2. 验证订单状态是否可评价（已完成）
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException(40003, "订单尚未完成，无法评价");
        }

        // 3. 检查该商品是否已评价
        List<OrderItem> orderItems = orderMapper.selectOrderItems(order.getId());
        OrderItem targetItem = orderItems.stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(40400, "订单中不包含该商品"));

        Review existingReview = reviewMapper.findByOrderItemId(targetItem.getId());
        if (existingReview != null) {
            throw new BusinessException(40003, "该商品已评价");
        }

        // 4. 创建评论
        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setOrderItemId(targetItem.getId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setAnonymous(request.getAnonymous() != null && request.getAnonymous() ? 1 : 0);
        review.setStatus("APPROVED"); // 默认审核通过（可改为PENDING需要审核）

        // 保存图片列表为JSON
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                review.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                review.setImages("[]");
            }
        }

        reviewMapper.insert(review);
    }

    @Override
    public PageResponse<ReviewVO> getUserReviews(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.selectByUserId(userId, offset, pageSize);
        int total = reviewMapper.countByUserId(userId);

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, pageSize);
    }

    @Override
    public List<PendingReviewItemVO> getPendingReviewItems(Long userId) {
        // 获取用户已完成但未全部评价的订单
        List<Order> completedOrders = orderMapper.selectByUserId(userId, "COMPLETED", 0, 100);
        List<PendingReviewItemVO> pendingItems = new ArrayList<>();

        for (Order order : completedOrders) {
            List<OrderItem> orderItems = orderMapper.selectOrderItems(order.getId());
            for (OrderItem item : orderItems) {
                // 检查该订单项是否已评价
                Review review = reviewMapper.findByOrderItemId(item.getId());
                if (review == null) {
                    PendingReviewItemVO vo = new PendingReviewItemVO();
                    vo.setOrderId(order.getId());
                    vo.setOrderNo(order.getOrderNo());
                    vo.setOrderItemId(item.getId());
                    vo.setProductId(item.getProductId());
                    vo.setProductName(item.getTitleSnapshot());
                    vo.setSkuName(item.getSkuNameSnapshot());
                    vo.setImage(item.getImageSnapshot());
                    pendingItems.add(vo);
                }
            }
        }

        return pendingItems;
    }

    @Override
    public List<Long> getFullyReviewedOrderIds(Long userId) {
        // 获取用户所有已完成的订单
        List<Order> completedOrders = orderMapper.selectByUserId(userId, "COMPLETED", 0, 1000);
        List<Long> fullyReviewedOrderIds = new ArrayList<>();

        for (Order order : completedOrders) {
            List<OrderItem> orderItems = orderMapper.selectOrderItems(order.getId());
            boolean allReviewed = true;
            for (OrderItem item : orderItems) {
                Review review = reviewMapper.findByOrderItemId(item.getId());
                if (review == null) {
                    allReviewed = false;
                    break;
                }
            }
            if (allReviewed && !orderItems.isEmpty()) {
                fullyReviewedOrderIds.add(order.getId());
            }
        }

        return fullyReviewedOrderIds;
    }

    @Override
    public PageResponse<ReviewVO> getProductReviews(Long productId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.findByProductId(productId, offset, pageSize);
        long total = reviewMapper.countApprovedByProductId(productId);

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, (int) total, page, pageSize);
    }

    @Override
    @Transactional
    public void auditReview(Long reviewId, String status, String rejectReason) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(40400, "评论不存在");
        }
        reviewMapper.updateStatus(reviewId, status, rejectReason);
    }

    @Override
    @Transactional
    public void replyReview(Long merchantId, Long reviewId, String content) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(40400, "评论不存在");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setMerchantId(merchantId);
        reply.setContent(content);
        reviewMapper.insertReply(reply);
    }

    @Override
    public PageResponse<ReviewVO> getMerchantReviews(Long merchantId, String status, Boolean hasReply,
            Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.selectByMerchant(merchantId, status, hasReply, offset, pageSize);
        int total = reviewMapper.countByMerchant(merchantId, status, hasReply);

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, pageSize);
    }

    @Override
    public PageResponse<ReviewVO> getAdminReviews(String status, Long productId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        Integer statusInt = parseStatus(status);
        List<Review> reviews = reviewMapper.selectByAdmin(statusInt, productId, offset, pageSize);
        int total = reviewMapper.countByAdmin(statusInt, productId);

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, pageSize);
    }

    /**
     * 将Review实体转换为ReviewVO
     */
    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setUserId(review.getUserId());
        vo.setProductId(review.getProductId());
        vo.setRating(review.getRating());
        vo.setContent(review.getContent());
        vo.setCreatedAt(review.getCreatedAt());
        vo.setAnonymous(review.getAnonymous() != null && review.getAnonymous() == 1);

        // 设置状态
        vo.setStatus(parseStatusToInt(review.getStatus()));
        vo.setStatusText(review.getStatus());

        // 解析图片列表
        if (review.getImages() != null && !review.getImages().isEmpty()) {
            try {
                vo.setImages(objectMapper.readValue(review.getImages(), new TypeReference<List<String>>() {
                }));
            } catch (JsonProcessingException e) {
                vo.setImages(Collections.emptyList());
            }
        } else {
            vo.setImages(Collections.emptyList());
        }

        // 获取用户信息
        User user = userMapper.findById(review.getUserId());
        if (user != null) {
            if (vo.getAnonymous()) {
                vo.setUserName("匿名用户");
                vo.setUserAvatar(null);
            } else {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            }
        }

        // 获取商品信息
        Product product = productMapper.findById(review.getProductId());
        if (product != null) {
            vo.setProductName(product.getTitle());
            vo.setProductImage(product.getMainImage());
        }

        // 获取回复列表
        List<ReviewReply> replies = reviewMapper.findRepliesByReviewId(review.getId());
        if (replies != null && !replies.isEmpty()) {
            List<ReviewVO.ReviewReplyVO> replyVOs = replies.stream().map(reply -> {
                ReviewVO.ReviewReplyVO replyVO = new ReviewVO.ReviewReplyVO();
                replyVO.setId(reply.getId());
                replyVO.setContent(reply.getContent());
                replyVO.setMerchantId(reply.getMerchantId());
                replyVO.setCreatedAt(reply.getCreatedAt());

                // 获取商家名称
                Merchant merchant = merchantMapper.findById(reply.getMerchantId());
                if (merchant != null) {
                    replyVO.setMerchantName(merchant.getShopName());
                }
                return replyVO;
            }).collect(Collectors.toList());
            vo.setReplies(replyVOs);
        } else {
            vo.setReplies(Collections.emptyList());
        }

        return vo;
    }

    /**
     * 解析状态字符串为整数
     */
    private Integer parseStatus(String status) {
        if (status == null)
            return null;
        switch (status.toUpperCase()) {
            case "PENDING":
                return 0;
            case "APPROVED":
                return 1;
            case "REJECTED":
                return 2;
            default:
                return null;
        }
    }

    /**
     * 解析状态字符串为整数（用于VO）
     */
    private Integer parseStatusToInt(String status) {
        if (status == null)
            return 0;
        switch (status.toUpperCase()) {
            case "PENDING":
                return 0;
            case "APPROVED":
                return 1;
            case "REJECTED":
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public PageResponse<ReviewVO> getPendingReviews(Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.findByStatus("PENDING", offset, pageSize);
        long total = reviewMapper.countByStatus("PENDING");

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, (int) total, page, pageSize);
    }

    @Override
    @Transactional
    public void auditReview(Long reviewId, org.example.teasystem.dto.request.ReviewAuditRequest request) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(40400, "评论不存在");
        }
        String status = request.getStatus() == 1 ? "APPROVED" : "REJECTED";
        String reason = request.getStatus() == 2 ? request.getReason() : null;
        reviewMapper.updateStatus(reviewId, status, reason);
    }

    @Override
    public PageResponse<ReviewVO> getReviewsByAdmin(Integer status, Long productId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Review> reviews = reviewMapper.selectByAdmin(status, productId, offset, pageSize);
        int total = reviewMapper.countByAdmin(status, productId);

        List<ReviewVO> voList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, pageSize);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(40400, "评论不存在");
        }
        reviewMapper.delete(reviewId);
    }
}
