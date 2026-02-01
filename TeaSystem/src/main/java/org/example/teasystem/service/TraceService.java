package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ProductTrace;

/**
 * 商品溯源服务接口
 */
public interface TraceService {

    /**
     * 通过溯源码查询溯源信息（用户端）
     */
    ProductTrace getByTraceCode(String traceCode);

    /**
     * 通过商品ID查询溯源信息（用户端）
     */
    ProductTrace getByProductId(Long productId);

    /**
     * 获取商家溯源列表
     */
    PageResult<ProductTrace> getMerchantTraceList(Long merchantId, String status, Integer page, Integer pageSize);

    /**
     * 创建溯源（商家）
     */
    Long createTrace(ProductTrace trace);

    /**
     * 更新溯源（商家）
     */
    void updateTrace(ProductTrace trace);

    /**
     * 提交审核（商家）
     */
    void submitForAudit(Long merchantId, Long id);

    /**
     * 删除溯源（商家）
     */
    void deleteTrace(Long merchantId, Long id);

    /**
     * 获取待审核列表（管理端）
     */
    PageResult<ProductTrace> getAuditList(String status, Integer page, Integer pageSize);

    /**
     * 审核通过（管理端）
     */
    void approve(Long id);

    /**
     * 审核拒绝（管理端）
     */
    void reject(Long id, String reason);
}
