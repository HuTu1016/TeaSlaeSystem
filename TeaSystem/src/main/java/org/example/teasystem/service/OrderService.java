package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.order.*;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    OrderCreateResponse createOrder(Long userId, OrderCreateRequest request, String idempotentKey);

    /**
     * 获取订单详情
     */
    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    /**
     * 分页查询用户订单
     */
    PageResult<OrderListVO> getUserOrders(Long userId, String status, Integer page, Integer pageSize);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 确认收货
     */
    void confirmReceipt(Long userId, Long orderId);

    /**
     * 分页查询商家订单
     */
    PageResult<OrderListVO> getMerchantOrders(Long merchantId, String status, Integer page, Integer pageSize);

    /**
     * 商家发货
     */
    void shipOrder(Long merchantId, Long orderId, ShipRequest request);

    /**
     * 支付订单
     * 
     * @param userId        用户ID
     * @param orderId       订单ID
     * @param paymentMethod 支付方式：DIRECT-直接支付，BALANCE-余额支付
     */
    void payOrder(Long userId, Long orderId, String paymentMethod);

    /**
     * 获取用户订单支出明细列表（用于会员中心）
     * 
     * @param userId     用户ID
     * @param filterType 筛选类型：ALL-全部，PAYMENT-支出，REFUND-收入（退款）
     * @param page       页码
     * @param pageSize   每页条数
     * @return 支出明细列表
     */
    PageResult<OrderExpenseVO> getOrderExpenseList(Long userId, String filterType, Integer page, Integer pageSize);
}
