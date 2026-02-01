package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.aftersale.*;
import org.example.teasystem.entity.AfterSale;
import org.example.teasystem.entity.Order;
import org.example.teasystem.entity.OrderItem;
import org.example.teasystem.mapper.AfterSaleMapper;
import org.example.teasystem.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 售后服务实现类
 */
@Service
public class AfterSaleServiceImpl implements org.example.teasystem.service.AfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private org.example.teasystem.mapper.CouponMapper couponMapper;

    // 售后状态常量（与数据库enum一致）
    private static final String STATUS_APPLIED = "APPLIED"; // 已申请，待商家处理
    private static final String STATUS_MERCHANT_APPROVED = "MERCHANT_APPROVED"; // 商家已同意
    private static final String STATUS_MERCHANT_REJECTED = "MERCHANT_REJECTED"; // 商家已拒绝
    private static final String STATUS_BUYER_SHIPPED_BACK = "BUYER_SHIPPED_BACK"; // 用户已寄回（退货退款）
    private static final String STATUS_MERCHANT_RECEIVED = "MERCHANT_RECEIVED"; // 商家已收货
    private static final String STATUS_REFUNDING = "REFUNDING"; // 退款中
    private static final String STATUS_COMPLETED = "COMPLETED"; // 已完成
    private static final String STATUS_CANCELLED = "CANCELLED"; // 用户已取消

    @Override
    @Transactional
    public Long applyAfterSale(Long userId, AfterSaleApplyRequest request) {
        // 查询订单项
        OrderItem orderItem = orderMapper.selectOrderItemById(request.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException(404, "订单项不存在");
        }

        // 查询订单
        Order order = orderMapper.selectById(orderItem.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权申请售后");
        }

        // 验证订单状态：已完成、已发货、待发货（已支付）的订单可以申请售后
        if (!"COMPLETED".equals(order.getStatus()) && !"SHIPPED".equals(order.getStatus())
                && !"PAID".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许申请售后");
        }

        // 检查是否已有售后
        AfterSale existAfterSale = afterSaleMapper.selectByOrderItemId(request.getOrderItemId());
        if (existAfterSale != null && !STATUS_CANCELLED.equals(existAfterSale.getStatus())
                && !STATUS_MERCHANT_REJECTED.equals(existAfterSale.getStatus())) {
            throw new BusinessException(400, "该商品已有售后申请");
        }

        // 创建售后
        AfterSale afterSale = new AfterSale();
        afterSale.setAfterSaleNo(generateAfterSaleNo());
        afterSale.setOrderId(order.getId());
        afterSale.setOrderItemId(request.getOrderItemId());
        afterSale.setUserId(userId);
        afterSale.setMerchantId(order.getMerchantId());
        afterSale.setType(request.getType());
        afterSale.setReason(request.getReason());
        afterSale.setDescription(request.getDescription());
        afterSale.setApplyAmount(request.getApplyAmount());
        afterSale.setStatus(STATUS_APPLIED);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            afterSale.setImages(String.join(",", request.getImages()));
        }

        afterSaleMapper.insert(afterSale);

        return afterSale.getId();
    }

    @Override
    public AfterSaleVO getAfterSaleDetail(Long userId, Long afterSaleId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该售后");
        }

        return convertToVO(afterSale);
    }

    @Override
    public PageResult<AfterSaleListVO> getUserAfterSales(Long userId, String status,
            Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        List<AfterSale> list = afterSaleMapper.selectByUserId(userId, status, offset, pageSize);
        long total = afterSaleMapper.countByUserId(userId, status);

        List<AfterSaleListVO> voList = list.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, voList);
    }

    @Override
    @Transactional
    public void cancelAfterSale(Long userId, Long afterSaleId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        if (!STATUS_APPLIED.equals(afterSale.getStatus())) {
            throw new BusinessException(400, "只能取消待处理的售后申请");
        }

        afterSaleMapper.updateStatus(afterSaleId, STATUS_CANCELLED);
    }

    @Override
    public PageResult<AfterSaleListVO> getMerchantAfterSales(Long merchantId, String status,
            Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        List<AfterSale> list = afterSaleMapper.selectByMerchantId(merchantId, status, offset, pageSize);
        long total = afterSaleMapper.countByMerchantId(merchantId, status);

        List<AfterSaleListVO> voList = list.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, voList);
    }

    @Override
    @Transactional
    public void approveAfterSale(Long merchantId, Long afterSaleId, String comment) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        if (!STATUS_APPLIED.equals(afterSale.getStatus())) {
            throw new BusinessException(400, "只能处理待处理的售后申请");
        }

        afterSaleMapper.updateStatusWithComment(afterSaleId, STATUS_MERCHANT_APPROVED, comment);
    }

    @Override
    @Transactional
    public void rejectAfterSale(Long merchantId, Long afterSaleId, String comment) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        if (!STATUS_APPLIED.equals(afterSale.getStatus())) {
            throw new BusinessException(400, "只能处理待处理的售后申请");
        }

        afterSaleMapper.updateStatusWithComment(afterSaleId, STATUS_MERCHANT_REJECTED, comment);
    }

    @Override
    @Transactional
    public void confirmReceived(Long merchantId, Long afterSaleId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        if (!STATUS_BUYER_SHIPPED_BACK.equals(afterSale.getStatus())) {
            throw new BusinessException(400, "售后状态不正确，需等待用户寄回商品");
        }

        // 更新状态为商家已收货
        afterSaleMapper.updateStatus(afterSaleId, STATUS_MERCHANT_RECEIVED);
    }

    @Override
    @Transactional
    public void refund(Long merchantId, Long afterSaleId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        // 仅退款：商家同意后直接退款
        // 退货退款：商家收货后才能退款
        String type = afterSale.getType();
        String status = afterSale.getStatus();

        if ("REFUND_ONLY".equals(type)) {
            // 仅退款，商家同意后直接退款
            if (!STATUS_MERCHANT_APPROVED.equals(status)) {
                throw new BusinessException(400, "仅退款申请需商家同意后才能执行退款");
            }
        } else {
            // 退货退款，需商家确认收货后才能退款
            if (!STATUS_MERCHANT_RECEIVED.equals(status)) {
                throw new BusinessException(400, "退货退款申请需商家确认收货后才能执行退款");
            }
        }

        // 恢复优惠券（如果订单使用了优惠券）
        Order order = orderMapper.selectById(afterSale.getOrderId());
        if (order != null && order.getCouponId() != null) {
            org.example.teasystem.entity.UserCoupon userCoupon = couponMapper.selectUserCouponByOrderId(order.getId());
            if (userCoupon != null) {
                couponMapper.restoreUserCoupon(userCoupon.getId());
            }
        }

        afterSaleMapper.updateStatus(afterSaleId, STATUS_COMPLETED);
    }

    @Override
    @Transactional
    public void buyerShip(Long userId, Long afterSaleId, AfterSaleShipRequest request) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }

        if (!afterSale.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该售后");
        }

        // 只有退货退款类型且商家已同意才能填写物流
        if (!"RETURN_REFUND".equals(afterSale.getType())) {
            throw new BusinessException(400, "仅退货退款申请需要填写物流信息");
        }

        if (!STATUS_MERCHANT_APPROVED.equals(afterSale.getStatus())) {
            throw new BusinessException(400, "只有商家同意后才能填写退货物流");
        }

        // 更新状态为用户已寄回
        afterSaleMapper.updateStatus(afterSaleId, STATUS_BUYER_SHIPPED_BACK);
    }

    /**
     * 生成售后单号
     */
    private String generateAfterSaleNo() {
        return "AS" + System.currentTimeMillis() +
                String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 转换为VO
     */
    private AfterSaleVO convertToVO(AfterSale afterSale) {
        AfterSaleVO vo = new AfterSaleVO();
        BeanUtils.copyProperties(afterSale, vo);

        // 处理图片列表
        if (afterSale.getImages() != null && !afterSale.getImages().isEmpty()) {
            vo.setImages(Arrays.asList(afterSale.getImages().split(",")));
        } else {
            vo.setImages(new ArrayList<>());
        }

        // 查询订单项信息，填充商品数据
        OrderItem orderItem = orderMapper.selectOrderItemById(afterSale.getOrderItemId());
        if (orderItem != null) {
            vo.setProductName(orderItem.getTitleSnapshot());
            vo.setProductImage(orderItem.getImageSnapshot());
            vo.setSkuName(orderItem.getSkuNameSnapshot());
            vo.setPrice(orderItem.getPrice());
            vo.setQuantity(orderItem.getQuantity());
        }

        return vo;
    }

    /**
     * 转换为列表VO
     */
    private AfterSaleListVO convertToListVO(AfterSale afterSale) {
        AfterSaleListVO vo = new AfterSaleListVO();
        vo.setId(afterSale.getId());
        vo.setAfterSaleNo(afterSale.getAfterSaleNo());
        vo.setType(afterSale.getType());
        vo.setStatus(afterSale.getStatus());
        vo.setReason(afterSale.getReason());
        vo.setApplyAmount(afterSale.getApplyAmount());
        vo.setCreatedAt(afterSale.getCreatedAt());

        // 查询订单项信息，填充商品数据
        OrderItem orderItem = orderMapper.selectOrderItemById(afterSale.getOrderItemId());
        if (orderItem != null) {
            vo.setProductName(orderItem.getTitleSnapshot());
            vo.setProductImage(orderItem.getImageSnapshot());
        }

        return vo;
    }
}
