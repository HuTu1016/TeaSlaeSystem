package org.example.teasystem.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ProductTrace;
import org.example.teasystem.mapper.TraceMapper;
import org.example.teasystem.service.TraceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品溯源服务实现
 */
@Service
@RequiredArgsConstructor
public class TraceServiceImpl implements TraceService {

    private final TraceMapper traceMapper;

    @Override
    public ProductTrace getByTraceCode(String traceCode) {
        ProductTrace trace = traceMapper.selectByTraceCode(traceCode);
        if (trace == null || !"APPROVED".equals(trace.getStatus())) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        return trace;
    }

    @Override
    public ProductTrace getByProductId(Long productId) {
        ProductTrace trace = traceMapper.selectByProductId(productId);
        if (trace == null) {
            throw new BusinessException(404, "该商品暂无溯源信息");
        }
        return trace;
    }

    @Override
    public PageResult<ProductTrace> getMerchantTraceList(Long merchantId, String status, Integer page,
            Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<ProductTrace> list = traceMapper.selectByMerchantId(merchantId, status, offset, pageSize);
        long total = traceMapper.countByMerchantId(merchantId, status);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public Long createTrace(ProductTrace trace) {
        // 生成唯一溯源码
        trace.setTraceCode("TC" + IdUtil.getSnowflakeNextIdStr());
        trace.setStatus("DRAFT");
        traceMapper.insert(trace);
        return trace.getId();
    }

    @Override
    public void updateTrace(ProductTrace trace) {
        ProductTrace existing = traceMapper.selectById(trace.getId());
        if (existing == null) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        if (!existing.getMerchantId().equals(trace.getMerchantId())) {
            throw new BusinessException(403, "无权操作");
        }
        if ("APPROVED".equals(existing.getStatus())) {
            throw new BusinessException(400, "已审核通过的溯源信息不能修改");
        }
        traceMapper.update(trace);
    }

    @Override
    public void submitForAudit(Long merchantId, Long id) {
        ProductTrace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        if (!trace.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作");
        }
        if (!"DRAFT".equals(trace.getStatus()) && !"REJECTED".equals(trace.getStatus())) {
            throw new BusinessException(400, "当前状态不能提交审核");
        }
        traceMapper.updateStatus(id, "PENDING");
    }

    @Override
    public void deleteTrace(Long merchantId, Long id) {
        ProductTrace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        if (!trace.getMerchantId().equals(merchantId)) {
            throw new BusinessException(403, "无权操作");
        }
        if ("APPROVED".equals(trace.getStatus())) {
            throw new BusinessException(400, "已审核通过的溯源信息不能删除");
        }
        traceMapper.deleteById(id);
    }

    @Override
    public PageResult<ProductTrace> getAuditList(String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<ProductTrace> list = traceMapper.selectList(status, offset, pageSize);
        long total = traceMapper.count(status);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public void approve(Long id) {
        ProductTrace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        if (!"PENDING".equals(trace.getStatus())) {
            throw new BusinessException(400, "当前状态不能审核");
        }
        traceMapper.updateStatus(id, "APPROVED");
    }

    @Override
    public void reject(Long id, String reason) {
        ProductTrace trace = traceMapper.selectById(id);
        if (trace == null) {
            throw new BusinessException(404, "溯源信息不存在");
        }
        if (!"PENDING".equals(trace.getStatus())) {
            throw new BusinessException(400, "当前状态不能审核");
        }
        traceMapper.updateAudit(id, "REJECTED", reason);
    }
}
