package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.dto.response.MerchantVO;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.entity.User;
import org.example.teasystem.mapper.MerchantMapper;
import org.example.teasystem.mapper.UserMapper;
import org.example.teasystem.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家服务实现类
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private org.example.teasystem.mapper.ProductMapper productMapper;

    @Autowired
    private org.example.teasystem.mapper.OrderMapper orderMapper;

    @Autowired
    private org.example.teasystem.mapper.AfterSaleMapper afterSaleMapper;

    @Autowired
    private org.example.teasystem.mapper.ReviewMapper reviewMapper;

    @Override
    public PageResponse<MerchantVO> getMerchantList(String keyword, Integer status,
            Integer page, Integer size) {
        int offset = (page - 1) * size;

        List<Merchant> merchants = merchantMapper.selectList(keyword, status, offset, size);
        int total = merchantMapper.count(keyword, status);

        List<MerchantVO> voList = merchants.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, size);
    }

    @Override
    public MerchantVO getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }
        return convertToVO(merchant);
    }

    @Override
    @Transactional
    public void auditMerchant(Long merchantId, Integer status, String reason) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }

        merchantMapper.updateStatus(merchantId, status);
        // TODO: 记录审核日志
    }

    @Override
    @Transactional
    public void updateMerchantStatus(Long merchantId, Integer status) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(404, "商家不存在");
        }

        merchantMapper.updateStatus(merchantId, status);
    }

    /**
     * 转换为VO
     */
    private MerchantVO convertToVO(Merchant merchant) {
        MerchantVO vo = new MerchantVO();

        // 手动映射字段
        vo.setId(merchant.getId());
        vo.setShopName(merchant.getShopName());
        vo.setContactPhone(merchant.getContactPhone()); // 设置联系电话
        vo.setPhone(merchant.getContactPhone()); // 兼容旧字段
        vo.setBusinessLicense(merchant.getBusinessLicense());
        vo.setLogo(merchant.getShopLogo());
        vo.setDescription(merchant.getShopDesc());
        vo.setStatus(merchant.getStatus());
        vo.setCreatedAt(merchant.getCreatedAt());

        // 从 user 表获取商家姓名
        if (merchant.getUserId() != null) {
            User user = userMapper.findById(merchant.getUserId());
            if (user != null) {
                vo.setContactName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }
        }

        // 设置状态文本
        switch (merchant.getStatus()) {
            case 0:
                vo.setStatusText("待审核");
                break;
            case 1:
                vo.setStatusText("正常");
                break;
            case 2:
                vo.setStatusText("已禁用");
                break;
            default:
                vo.setStatusText("未知");
        }

        return vo;
    }

    @Override
    public org.example.teasystem.dto.response.MerchantStatsDTO getMerchantStats(Long merchantId) {
        org.example.teasystem.dto.response.MerchantStatsDTO stats = new org.example.teasystem.dto.response.MerchantStatsDTO();

        // 商品统计
        stats.setOnShelfProducts(productMapper.count(merchantId, null, "ON_SHELF", null, null, null, null, null));
        stats.setOffShelfProducts(productMapper.count(merchantId, null, "OFF_SHELF", null, null, null, null, null));
        stats.setPendingProducts(productMapper.countPendingByCondition(merchantId, null));
        stats.setTotalProducts(productMapper.count(merchantId, null, null, null, null, null, null, null));

        // 订单统计
        stats.setTotalOrders(orderMapper.countByMerchantId(merchantId, null));
        stats.setPendingOrders(orderMapper.countByMerchantId(merchantId, "PAID")); // 已支付待发货
        stats.setShippedOrders(orderMapper.countByMerchantId(merchantId, "SHIPPED"));
        stats.setCompletedOrders(orderMapper.countByMerchantId(merchantId, "COMPLETED"));
        stats.setTotalSales(orderMapper.sumSalesByMerchantId(merchantId));

        // 售后统计
        stats.setTotalAfterSales(afterSaleMapper.countByMerchantId(merchantId, null));
        stats.setPendingAfterSales(afterSaleMapper.countByMerchantId(merchantId, "PENDING"));

        // 评价统计 - 需要添加按商家查询的方法
        // 暂时使用0作为占位
        stats.setTotalReviews(0L);
        stats.setPendingReviews(0L);

        return stats;
    }
}
