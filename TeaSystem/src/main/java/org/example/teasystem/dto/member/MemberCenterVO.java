package org.example.teasystem.dto.member;

import lombok.Data;
import org.example.teasystem.entity.MembershipLevel;
import org.example.teasystem.entity.UserBalance;

import java.math.BigDecimal;

/**
 * 会员中心数据传输对象
 * 包含钱包余额、会员等级、进度等完整信息
 */
@Data
public class MemberCenterVO {

    /**
     * 钱包余额信息
     */
    private UserBalance balance;

    /**
     * 当前会员等级
     */
    private MembershipLevel currentLevel;

    /**
     * 下一会员等级（达到最高等级时为null）
     */
    private MembershipLevel nextLevel;

    /**
     * 累计消费金额
     */
    private BigDecimal totalExpense;

    /**
     * 距离下一等级还需消费金额
     */
    private BigDecimal nextLevelRequirement;

    /**
     * 当前等级进度百分比（0-100）
     */
    private Integer progressPercent;

    /**
     * 当前积分
     */
    private Integer points;
}
