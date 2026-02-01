package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.dto.member.MemberCenterVO;
import org.example.teasystem.entity.MembershipLevel;
import org.example.teasystem.entity.UserBalance;
import org.example.teasystem.entity.UserMembership;
import org.example.teasystem.mapper.MemberMapper;
import org.example.teasystem.service.BalanceService;
import org.example.teasystem.service.MemberService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 会员服务实现
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Lazy
    private final BalanceService balanceService;

    @Override
    public MembershipLevel getUserLevel(Long userId) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        if (membership == null) {
            // 返回默认等级（青铜会员）
            return memberMapper.selectLevelByCode("BRONZE");
        }
        return memberMapper.selectLevelById(membership.getLevelId());
    }

    @Override
    public Integer getUserPoints(Long userId) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        return membership != null ? membership.getPoints() : 0;
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        if (membership == null) {
            initUserMembership(userId);
        }
        memberMapper.addPoints(userId, points);
        // 刷新等级
        refreshUserLevel(userId);
    }

    @Override
    public BigDecimal calculateMemberDiscount(Long userId, BigDecimal amount) {
        MembershipLevel level = getUserLevel(userId);
        if (level == null || level.getDiscountRate() == null) {
            return BigDecimal.ZERO;
        }

        // discountRate是折扣率，100表示无折扣，98表示98折
        BigDecimal discountRate = level.getDiscountRate();
        if (discountRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.ZERO;
        }

        // 计算折扣金额 = 原价 * (100 - 折扣率) / 100
        BigDecimal discountPercent = BigDecimal.valueOf(100).subtract(discountRate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return amount.multiply(discountPercent).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void initUserMembership(Long userId) {
        UserMembership existing = memberMapper.selectByUserId(userId);
        if (existing != null) {
            return;
        }

        // 获取默认等级（青铜会员）
        MembershipLevel defaultLevel = memberMapper.selectLevelByCode("BRONZE");
        Long levelId = defaultLevel != null ? defaultLevel.getId() : 1L;

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setLevelId(levelId);
        membership.setPoints(0);
        membership.setTotalExpense(BigDecimal.ZERO);
        memberMapper.insert(membership);
    }

    @Override
    @Transactional
    public void refreshUserLevel(Long userId) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        if (membership == null) {
            return;
        }

        // 根据积分获取应该的等级
        MembershipLevel targetLevel = memberMapper.selectLevelByPoints(membership.getPoints());
        if (targetLevel != null && !targetLevel.getId().equals(membership.getLevelId())) {
            membership.setLevelId(targetLevel.getId());
            memberMapper.update(membership);
        }
    }

    @Override
    @Transactional
    public void addExpenseAndRefreshLevel(Long userId, BigDecimal amount) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        if (membership == null) {
            initUserMembership(userId);
        }
        // 增加累计消费金额
        memberMapper.addExpense(userId, amount);
        // 根据消费刷新等级
        refreshUserLevelByExpense(userId);
    }

    @Override
    @Transactional
    public void refreshUserLevelByExpense(Long userId) {
        UserMembership membership = memberMapper.selectByUserId(userId);
        if (membership == null) {
            return;
        }

        BigDecimal totalExpense = membership.getTotalExpense();
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        // 根据累计消费金额获取应该的等级
        MembershipLevel targetLevel = memberMapper.selectLevelByExpense(totalExpense);
        if (targetLevel != null && !targetLevel.getId().equals(membership.getLevelId())) {
            membership.setLevelId(targetLevel.getId());
            memberMapper.update(membership);
        }
    }

    @Override
    public UserMembership getUserMembership(Long userId) {
        return memberMapper.selectByUserId(userId);
    }

    @Override
    public MemberCenterVO getMemberCenterInfo(Long userId) {
        MemberCenterVO vo = new MemberCenterVO();

        // 获取钱包余额信息
        UserBalance balance = balanceService.getBalance(userId);
        vo.setBalance(balance);

        // 获取用户会员信息
        UserMembership membership = memberMapper.selectByUserId(userId);
        BigDecimal totalExpense = BigDecimal.ZERO;
        Integer points = 0;

        if (membership != null) {
            totalExpense = membership.getTotalExpense() != null ? membership.getTotalExpense() : BigDecimal.ZERO;
            points = membership.getPoints() != null ? membership.getPoints() : 0;
        }
        vo.setTotalExpense(totalExpense);
        vo.setPoints(points);

        // 获取当前等级
        MembershipLevel currentLevel = getUserLevel(userId);
        vo.setCurrentLevel(currentLevel);

        // 获取下一等级
        MembershipLevel nextLevel = memberMapper.selectNextLevel(totalExpense);
        vo.setNextLevel(nextLevel);

        // 计算距离下一等级还需消费金额和进度百分比
        if (nextLevel != null && currentLevel != null) {
            BigDecimal currentThreshold = BigDecimal.valueOf(currentLevel.getMinPoints());
            BigDecimal nextThreshold = BigDecimal.valueOf(nextLevel.getMinPoints());

            // 距离下一等级还需消费
            BigDecimal requirement = nextThreshold.subtract(totalExpense);
            if (requirement.compareTo(BigDecimal.ZERO) < 0) {
                requirement = BigDecimal.ZERO;
            }
            vo.setNextLevelRequirement(requirement);

            // 计算进度百分比
            BigDecimal range = nextThreshold.subtract(currentThreshold);
            if (range.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal progress = totalExpense.subtract(currentThreshold);
                if (progress.compareTo(BigDecimal.ZERO) < 0) {
                    progress = BigDecimal.ZERO;
                }
                int percent = progress.multiply(BigDecimal.valueOf(100))
                        .divide(range, 0, RoundingMode.HALF_UP)
                        .intValue();
                vo.setProgressPercent(Math.min(percent, 100));
            } else {
                vo.setProgressPercent(100);
            }
        } else {
            // 已达最高等级
            vo.setNextLevelRequirement(BigDecimal.ZERO);
            vo.setProgressPercent(100);
        }

        return vo;
    }
}
