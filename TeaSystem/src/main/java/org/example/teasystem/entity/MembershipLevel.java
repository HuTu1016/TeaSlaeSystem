package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级实体
 */
@Data
public class MembershipLevel implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 等级ID
     */
    private Long id;
    
    /**
     * 等级编码
     */
    private String code;
    
    /**
     * 等级名称
     */
    private String name;
    
    /**
     * 折扣率（100表示无折扣）
     */
    private BigDecimal discountRate;
    
    /**
     * 权益JSON
     */
    private String benefitsJson;
    
    /**
     * 所需最低积分
     */
    private Integer minPoints;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
