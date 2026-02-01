package org.example.teasystem.service;

import org.example.teasystem.dto.response.MerchantVO;
import org.example.teasystem.dto.response.PageResponse;

/**
 * 商家服务接口
 */
public interface MerchantService {

    /**
     * 获取商家列表（管理员）
     */
    PageResponse<MerchantVO> getMerchantList(String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取商家详情
     */
    MerchantVO getMerchantDetail(Long merchantId);

    /**
     * 审核商家入驻（管理员）
     */
    void auditMerchant(Long merchantId, Integer status, String reason);

    /**
     * 更新商家状态
     */
    void updateMerchantStatus(Long merchantId, Integer status);

    /**
     * 获取商家统计数据
     */
    org.example.teasystem.dto.response.MerchantStatsDTO getMerchantStats(Long merchantId);
}
