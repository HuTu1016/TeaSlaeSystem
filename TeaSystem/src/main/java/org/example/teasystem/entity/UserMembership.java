package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户会员关联实体
 */
@Data
public class UserMembership implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 等级ID
     */
    private Long levelId;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 累计消费金额
     */
    private BigDecimal totalExpense;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
