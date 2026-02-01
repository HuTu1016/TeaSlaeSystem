package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.order.*;
import org.example.teasystem.entity.*;
import org.example.teasystem.mapper.*;
import org.example.teasystem.service.BalanceService;
import org.example.teasystem.service.MemberService;
import org.example.teasystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private MemberService memberService;

    private static final String IDEMPOTENCY_KEY_PREFIX = "order:idempotency:";

    @Override
    @Transactional
    public OrderCreateResponse createOrder(Long userId, OrderCreateRequest request, String idempotentKey) {
        // 幂等性检查
        if (idempotentKey != null && !idempotentKey.isEmpty()) {
            String key = IDEMPOTENCY_KEY_PREFIX + idempotentKey;
            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(exists)) {
                throw new BusinessException(400, "请勿重复提交订单");
            }
            redisTemplate.opsForValue().set(key, "1", 30, TimeUnit.MINUTES);
        }

        // 获取收货地址
        Address address = addressMapper.findById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "收货地址不存在");
        }

        // 根据请求中的SKU列表创建订单项
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(400, "请选择要购买的商品");
        }

        // 计算订单金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Long merchantId = null;

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            ProductSku sku = productSkuMapper.selectById(itemRequest.getSkuId());
            if (sku == null) {
                throw new BusinessException(400, "商品规格不存在");
            }

            Product product = productMapper.selectById(sku.getProductId());
            if (product == null || !"ON_SHELF".equals(product.getStatus())) {
                throw new BusinessException(400, "商品已下架：" + (product != null ? product.getTitle() : ""));
            }

            if (sku.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException(400, "库存不足：" + product.getTitle());
            }

            // 扣减库存
            int updated = productSkuMapper.decreaseStock(sku.getId(), itemRequest.getQuantity());
            if (updated == 0) {
                throw new BusinessException(400, "库存不足：" + product.getTitle());
            }

            // 记录商家ID
            if (merchantId == null) {
                merchantId = product.getMerchantId();
            }

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setSkuId(sku.getId());
            orderItem.setProductName(product.getTitle());
            orderItem.setSkuName(sku.getSkuName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setTitleSnapshot(product.getTitle());
            orderItem.setSkuNameSnapshot(sku.getSkuName());
            orderItem.setImageSnapshot(product.getMainImage());
            orderItem.setPrice(sku.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubtotal(sku.getPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // 处理优惠券
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        UserCoupon userCoupon = null;
        if (request.getCouponId() != null) {
            // 查询用户优惠券
            userCoupon = couponMapper.selectUserCouponById(request.getCouponId());
            if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
                throw new BusinessException(400, "优惠券不存在");
            }
            if (!"UNUSED".equals(userCoupon.getStatus())) {
                throw new BusinessException(400, "优惠券已使用或已过期");
            }
            if (userCoupon.getValidTo() != null && userCoupon.getValidTo().isBefore(java.time.LocalDateTime.now())) {
                throw new BusinessException(400, "优惠券已过期");
            }

            // 查询优惠券详情计算折扣
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon != null) {
                // 检查满减条件
                if (coupon.getMinAmount() != null && totalAmount.compareTo(coupon.getMinAmount()) < 0) {
                    throw new BusinessException(400, "订单金额未达到优惠券使用门槛");
                }
                // 计算折扣金额
                if ("AMOUNT".equals(coupon.getType())) {
                    // 满减券
                    couponDiscountAmount = coupon.getAmount();
                } else if ("PERCENT".equals(coupon.getType())) {
                    // 折扣券
                    BigDecimal discountRate = new BigDecimal(coupon.getDiscountPercent()).divide(new BigDecimal("100"));
                    couponDiscountAmount = totalAmount.multiply(discountRate);
                    // 限制最大折扣
                    if (coupon.getMaxDiscount() != null
                            && couponDiscountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                        couponDiscountAmount = coupon.getMaxDiscount();
                    }
                }
            }
        }

        // 计算实付金额
        BigDecimal payAmount = totalAmount.subtract(couponDiscountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setFreightAmount(BigDecimal.ZERO); // TODO: 运费计算
        order.setCouponId(request.getCouponId());
        order.setCouponDiscountAmount(couponDiscountAmount);
        order.setStatus("CREATED"); // 待支付
        order.setPayStatus("UNPAID"); // 未支付
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() +
                address.getDistrict() + address.getDetail());
        // 设置地址快照JSON
        String addressSnapshot = String.format(
                "{\"receiverName\":\"%s\",\"receiverPhone\":\"%s\",\"address\":\"%s\"}",
                address.getReceiverName(),
                address.getReceiverPhone(),
                address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setAddressSnapshot(addressSnapshot);
        order.setRemark(request.getRemark());

        orderMapper.insert(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderMapper.insertOrderItem(item);
        }

        // 标记优惠券为已使用
        if (userCoupon != null) {
            couponMapper.useUserCoupon(userCoupon.getId(), order.getId());
        }

        return OrderCreateResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .payAmount(order.getPayAmount())
                .build();
    }

    @Override
    public PageResult<OrderListVO> getUserOrders(Long userId, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        List<Order> orders = orderMapper.selectByUserId(userId, status, offset, pageSize);
        long total = orderMapper.countByUserId(userId, status);

        List<OrderListVO> voList = orders.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, voList);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        // 验证订单归属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该订单");
        }

        return convertToDetailVO(order);
    }

    @Override
    public PageResult<OrderListVO> getMerchantOrders(Long merchantId, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;

        List<Order> orders = orderMapper.selectByMerchantId(merchantId, status, offset, pageSize);
        long total = orderMapper.countByMerchantId(merchantId, status);

        List<OrderListVO> voList = orders.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, voList);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(400, "只能取消待支付的订单");
        }

        // 恢复库存
        List<OrderItem> items = orderMapper.selectOrderItems(orderId);
        for (OrderItem item : items) {
            productSkuMapper.increaseStock(item.getSkuId(), item.getQuantity());
        }

        // 恢复优惠券（如果订单使用了优惠券）
        if (order.getCouponId() != null) {
            UserCoupon userCoupon = couponMapper.selectUserCouponByOrderId(orderId);
            if (userCoupon != null) {
                couponMapper.restoreUserCoupon(userCoupon.getId());
            }
        }

        // 更新订单状态
        orderMapper.updateStatus(orderId, "CANCELLED");
    }

    @Override
    @Transactional
    public void confirmReceipt(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        if (!"SHIPPED".equals(order.getStatus())) {
            throw new BusinessException(400, "只能确认已发货的订单");
        }

        orderMapper.updateStatus(orderId, "COMPLETED");
    }

    @Override
    @Transactional
    public void shipOrder(Long merchantId, Long orderId, ShipRequest request) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!order.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException(400, "只能发货已支付的订单");
        }

        // 更新订单状态和物流信息
        orderMapper.updateShipment(orderId, request.getTrackingNo(), request.getCarrier());
        orderMapper.updateStatus(orderId, "SHIPPED");
    }

    @Override
    @Transactional
    public void payOrder(Long userId, Long orderId, String paymentMethod) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法支付");
        }

        BigDecimal payAmount = order.getPayAmount();

        // 余额支付：扣减用户余额
        if ("BALANCE".equals(paymentMethod)) {
            // 检查余额是否充足
            UserBalance userBalance = balanceService.getBalance(userId);
            if (userBalance == null || userBalance.getBalance().compareTo(payAmount) < 0) {
                throw new BusinessException(400, "余额不足，请选择其他支付方式");
            }
            // 扣减余额
            balanceService.decreaseBalance(userId, payAmount, "PAYMENT", "ORDER", order.getOrderNo(),
                    "订单支付：" + order.getOrderNo());
        }
        // DIRECT直接支付模式：模拟第三方支付成功

        // 更新订单状态为已支付
        orderMapper.updatePayStatus(orderId, "PAID", "PAID", java.time.LocalDateTime.now());

        // 累计消费金额并触发会员等级刷新
        memberService.addExpenseAndRefreshLevel(userId, payAmount);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "TEA" + System.currentTimeMillis() +
                String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 转换为订单列表VO
     */
    private OrderListVO convertToListVO(Order order) {
        OrderListVO vo = new OrderListVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setCreatedAt(order.getCreatedAt());

        // 查询订单项并转换
        List<OrderItem> items = orderMapper.selectOrderItems(order.getId());
        vo.setItemCount(items.size());

        // 填充订单商品项列表（包含售后状态）
        List<OrderListVO.OrderItemVO> itemVOList = items.stream()
                .map(item -> {
                    OrderListVO.OrderItemVO itemVO = new OrderListVO.OrderItemVO();
                    itemVO.setId(item.getId());
                    itemVO.setTitle(item.getTitleSnapshot());
                    itemVO.setSkuName(item.getSkuNameSnapshot());
                    itemVO.setImage(item.getImageSnapshot());
                    itemVO.setPrice(item.getPrice());
                    itemVO.setQuantity(item.getQuantity());

                    // 查询该订单项是否有正在处理的售后
                    AfterSale afterSale = afterSaleMapper.findPendingByOrderItemId(item.getId());
                    if (afterSale != null) {
                        itemVO.setAfterSaleId(afterSale.getId());
                        itemVO.setAfterSaleStatus(afterSale.getStatus());
                    }
                    return itemVO;
                })
                .collect(Collectors.toList());
        vo.setItems(itemVOList);

        // 检查是否有正在处理的售后
        boolean hasAfterSale = itemVOList.stream().anyMatch(item -> item.getAfterSaleStatus() != null);
        vo.setHasAfterSale(hasAfterSale);

        return vo;
    }

    /**
     * 转换为订单详情VO
     */
    private OrderDetailVO convertToDetailVO(Order order) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setCouponDiscountAmount(order.getCouponDiscountAmount()); // 优惠券抵扣金额
        vo.setRemark(order.getRemark());
        vo.setCreatedAt(order.getCreatedAt());

        // 从 address_snapshot JSON 中解析收货信息
        String addressJson = order.getAddressSnapshot();
        System.out.println("=== DEBUG: address_snapshot = " + addressJson); // 调试日志
        if (addressJson != null && !addressJson.isEmpty()) {
            try {
                // 使用 Jackson 解析 JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, String> addressMap = mapper.readValue(addressJson,
                        new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, String>>() {
                        });

                String receiverName = addressMap.get("receiverName");
                String receiverPhone = addressMap.get("receiverPhone");
                String address = addressMap.get("address");

                System.out.println("=== DEBUG: receiverName = " + receiverName); // 调试日志

                vo.setReceiverName(receiverName);
                vo.setReceiverPhone(receiverPhone);
                vo.setAddressDetail(address);

                // 设置地址快照
                OrderDetailVO.AddressSnapshot addressSnapshot = new OrderDetailVO.AddressSnapshot();
                addressSnapshot.setReceiverName(receiverName);
                addressSnapshot.setReceiverPhone(receiverPhone);
                vo.setAddress(addressSnapshot);
            } catch (Exception e) {
                System.out.println("=== DEBUG: JSON解析失败: " + e.getMessage()); // 调试日志
                e.printStackTrace();
            }
        }

        // 查询订单项
        List<OrderItem> items = orderMapper.selectOrderItems(order.getId());
        List<OrderDetailVO.OrderItemVO> itemVOList = items.stream()
                .map(item -> {
                    OrderDetailVO.OrderItemVO itemVO = new OrderDetailVO.OrderItemVO();
                    itemVO.setId(item.getId());
                    itemVO.setProductId(item.getProductId());
                    itemVO.setSkuId(item.getSkuId());
                    itemVO.setTitleSnapshot(item.getTitleSnapshot());
                    itemVO.setSkuNameSnapshot(item.getSkuNameSnapshot());
                    itemVO.setImageSnapshot(item.getImageSnapshot());
                    itemVO.setPrice(item.getPrice());
                    itemVO.setQuantity(item.getQuantity());
                    itemVO.setSubtotal(item.getSubtotal());
                    // 前端兼容字段
                    itemVO.setTitle(item.getTitleSnapshot());
                    itemVO.setImage(item.getImageSnapshot());
                    itemVO.setSkuName(item.getSkuNameSnapshot());
                    return itemVO;
                })
                .collect(Collectors.toList());
        vo.setItems(itemVOList);

        return vo;
    }

    /**
     * 从JSON字符串中提取指定key的value
     * 简单实现，仅支持 {"key":"value"} 格式
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return json.substring(startIndex, endIndex);
    }

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    @Override
    public PageResult<OrderExpenseVO> getOrderExpenseList(Long userId, String filterType, Integer page,
            Integer pageSize) {
        // 获取所有已支付订单和已完成退款
        List<OrderExpenseVO> allExpenses = new ArrayList<>();

        // 1. 获取所有已支付订单（作为支出）
        List<Order> paidOrders = orderMapper.selectPaidOrdersByUserId(userId, 0, 1000);
        for (Order order : paidOrders) {
            OrderExpenseVO expense = new OrderExpenseVO();
            expense.setOrderId(order.getId());
            expense.setOrderNo(order.getOrderNo());
            expense.setAmount(order.getPayAmount().negate()); // 支出为负
            expense.setExpenseType("PAYMENT");
            expense.setRemark("订单支付");
            expense.setCreatedAt(order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt());

            // 获取订单第一个商品信息
            List<OrderItem> items = orderMapper.selectOrderItems(order.getId());
            if (!items.isEmpty()) {
                expense.setProductTitle(items.get(0).getTitleSnapshot());
                expense.setProductImage(items.get(0).getImageSnapshot());
            }
            allExpenses.add(expense);
        }

        // 2. 获取已完成的退款（作为收入）
        List<AfterSale> completedRefunds = afterSaleMapper.selectByUserId(userId, "COMPLETED", 0, 1000);
        for (AfterSale afterSale : completedRefunds) {
            OrderExpenseVO expense = new OrderExpenseVO();
            expense.setOrderId(afterSale.getOrderId());

            // 获取订单号
            Order order = orderMapper.selectById(afterSale.getOrderId());
            if (order != null) {
                expense.setOrderNo(order.getOrderNo());
            }

            expense.setAmount(afterSale.getApplyAmount()); // 退款为正（收入）
            expense.setExpenseType("REFUND");
            expense.setRefundType(afterSale.getType());

            // 设置退款备注
            if ("REFUND_ONLY".equals(afterSale.getType())) {
                expense.setRemark("仅退款");
            } else if ("RETURN_REFUND".equals(afterSale.getType())) {
                expense.setRemark("退货退款");
            } else {
                expense.setRemark("售后退款");
            }

            expense.setCreatedAt(
                    afterSale.getCompletedAt() != null ? afterSale.getCompletedAt() : afterSale.getCreatedAt());

            // 获取订单项信息
            OrderItem orderItem = orderMapper.selectOrderItemById(afterSale.getOrderItemId());
            if (orderItem != null) {
                expense.setProductTitle(orderItem.getTitleSnapshot());
                expense.setProductImage(orderItem.getImageSnapshot());
            }
            allExpenses.add(expense);
        }

        // 3. 根据filterType过滤
        List<OrderExpenseVO> filteredList;
        if ("PAYMENT".equals(filterType)) {
            filteredList = allExpenses.stream()
                    .filter(e -> "PAYMENT".equals(e.getExpenseType()))
                    .collect(Collectors.toList());
        } else if ("REFUND".equals(filterType)) {
            filteredList = allExpenses.stream()
                    .filter(e -> "REFUND".equals(e.getExpenseType()))
                    .collect(Collectors.toList());
        } else {
            filteredList = allExpenses;
        }

        // 4. 按时间倒序排序
        filteredList.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // 5. 分页
        long total = filteredList.size();
        int offset = (page - 1) * pageSize;
        List<OrderExpenseVO> pagedList;
        if (offset >= total) {
            pagedList = new ArrayList<>();
        } else {
            int end = Math.min(offset + pageSize, (int) total);
            pagedList = filteredList.subList(offset, end);
        }

        PageResult<OrderExpenseVO> result = new PageResult<>();
        result.setItems(pagedList);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return result;
    }
}
