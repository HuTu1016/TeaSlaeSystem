package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.aftersale.*;

/**
 * 售后服务接口
 */
public interface AfterSaleService {

    /**
     * 申请售后
     */
    Long applyAfterSale(Long userId, AfterSaleApplyRequest request);

    /**
     * 获取售后详情
     */
    AfterSaleVO getAfterSaleDetail(Long userId, Long afterSaleId);

    /**
     * 分页查询用户售后
     */
    PageResult<AfterSaleListVO> getUserAfterSales(Long userId, String status, Integer page, Integer pageSize);

    /**
     * 取消售后
     */
    void cancelAfterSale(Long userId, Long afterSaleId);

    /**
     * 分页查询商家售后
     */
    PageResult<AfterSaleListVO> getMerchantAfterSales(Long merchantId, String status, Integer page, Integer pageSize);

    /**
     * 商家审核通过
     */
    void approveAfterSale(Long merchantId, Long afterSaleId, String comment);

    /**
     * 商家拒绝
     */
    void rejectAfterSale(Long merchantId, Long afterSaleId, String comment);

    /**
     * 商家确认收货（退货退款场景）
     */
    void confirmReceived(Long merchantId, Long afterSaleId);

    /**
     * 执行退款
     */
    void refund(Long merchantId, Long afterSaleId);

    /**
     * 用户填写退货物流信息（退货退款场景）
     */
    void buyerShip(Long userId, Long afterSaleId, AfterSaleShipRequest request);
}
