package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.dto.payment.PaymentCreateResponse;
import org.example.teasystem.dto.payment.PaymentNotifyRequest;
import org.example.teasystem.dto.payment.PaymentVO;
import org.example.teasystem.entity.Order;
import org.example.teasystem.entity.PaymentRecord;
import org.example.teasystem.mapper.OrderMapper;
import org.example.teasystem.mapper.PaymentRecordMapper;
import org.example.teasystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    @Transactional
    public PaymentCreateResponse createPayment(Long userId, Long orderId) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        if (!"0".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许支付");
        }

        // 检查是否已有支付中的记录
        PaymentRecord existRecord = paymentRecordMapper.selectPendingByOrderId(orderId);
        if (existRecord != null) {
            // 返回已有的支付信息
            return PaymentCreateResponse.builder()
                    .payNo(existRecord.getPayNo())
                    .channel(existRecord.getChannel())
                    .mockQr("https://mock-pay.example.com/qr/" + existRecord.getPayNo())
                    .build();
        }

        // 创建支付记录
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setPayNo(generatePaymentNo());
        record.setChannel("MOCK"); // 默认Mock支付
        record.setAmount(order.getPayAmount());
        record.setStatus("CREATED"); // 待支付

        paymentRecordMapper.insert(record);

        return PaymentCreateResponse.builder()
                .payNo(record.getPayNo())
                .channel(record.getChannel())
                .mockQr("https://mock-pay.example.com/qr/" + record.getPayNo())
                .build();
    }

    @Override
    @Transactional
    public void notify(String payNo, PaymentNotifyRequest request) {
        PaymentRecord record = paymentRecordMapper.selectByPaymentNo(payNo);
        if (record == null) {
            throw new BusinessException(404, "支付记录不存在");
        }

        if (!"CREATED".equals(record.getStatus())) {
            // 已处理过，忽略
            return;
        }

        // 更新支付记录
        record.setStatus("PAID");
        record.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : LocalDateTime.now());
        paymentRecordMapper.update(record);

        // 支付成功，更新订单状态
        orderMapper.updateStatus(record.getOrderId(), "1"); // 待发货
    }

    @Override
    public PaymentVO getPaymentByOrderId(Long orderId) {
        PaymentRecord record = paymentRecordMapper.selectByOrderId(orderId);
        if (record == null) {
            throw new BusinessException(404, "支付记录不存在");
        }

        return convertToVO(record);
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis() +
                String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 转换为VO
     */
    private PaymentVO convertToVO(PaymentRecord record) {
        PaymentVO vo = new PaymentVO();
        vo.setId(record.getId());
        vo.setPayNo(record.getPayNo());
        vo.setOrderId(record.getOrderId());
        vo.setChannel(record.getChannel());
        vo.setStatus(record.getStatus());
        vo.setAmount(record.getAmount());
        vo.setCreatedAt(record.getCreatedAt());
        vo.setPaidAt(record.getPaidAt());
        return vo;
    }
}
