package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品溯源实体
 */
@Data
public class ProductTrace implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 溯源ID
     */
    private Long id;
    
    /**
     * 溯源码
     */
    private String traceCode;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 批次号
     */
    private String batchNo;
    
    /**
     * 产区详细
     */
    private String origin;
    
    /**
     * 采摘日期
     */
    private LocalDate pickDate;
    
    /**
     * 工艺
     */
    private String process;
    
    /**
     * 生产商
     */
    private String producer;
    
    /**
     * 检测报告URL
     */
    private String inspectionReportUrl;
    
    /**
     * 证书URL
     */
    private String certificateUrl;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 审核状态：DRAFT/PENDING/APPROVED/REJECTED
     */
    private String status;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
