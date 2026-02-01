package org.example.teasystem.service;

import org.example.teasystem.dto.member.MemberCenterVO;
import org.example.teasystem.entity.MembershipLevel;
import org.example.teasystem.entity.UserMembership;

import java.math.BigDecimal;

/**
 * 会员服务接口
 */
public interface MemberService {

    /**
     * 获取用户会员等级
     */
    MembershipLevel getUserLevel(Long userId);

    /**
     * 获取用户积分
     */
    Integer getUserPoints(Long userId);

    /**
     * 增加用户积分
     */
    void addPoints(Long userId, Integer points);

    /**
     * 计算会员折扣
     */
    BigDecimal calculateMemberDiscount(Long userId, BigDecimal amount);

    /**
     * 初始化用户会员信息
     */
    void initUserMembership(Long userId);

    /**
     * 刷新用户会员等级（根据积分自动升级）
     */
    void refreshUserLevel(Long userId);

    /**
     * 增加累计消费金额并刷新会员等级
     */
    void addExpenseAndRefreshLevel(Long userId, BigDecimal amount);

    /**
     * 根据累计消费刷新会员等级
     */
    void refreshUserLevelByExpense(Long userId);

    /**
     * 获取用户会员信息
     */
    UserMembership getUserMembership(Long userId);

    /**
     * 获取会员中心完整信息（余额+等级+进度）
     */
    MemberCenterVO getMemberCenterInfo(Long userId);
}
