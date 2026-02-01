package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.MembershipLevel;
import org.example.teasystem.entity.UserMembership;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员Mapper
 */
@Mapper
public interface MemberMapper {

        /**
         * 根据ID查询会员等级
         */
        @Select("SELECT * FROM membership_level WHERE id = #{id}")
        MembershipLevel selectLevelById(@Param("id") Long id);

        /**
         * 根据编码查询会员等级
         */
        @Select("SELECT * FROM membership_level WHERE code = #{code}")
        MembershipLevel selectLevelByCode(@Param("code") String code);

        /**
         * 查询所有会员等级
         */
        @Select("SELECT * FROM membership_level ORDER BY sort ASC")
        List<MembershipLevel> selectAllLevels();

        /**
         * 根据积分查询对应等级
         */
        @Select("SELECT * FROM membership_level WHERE min_points <= #{points} ORDER BY min_points DESC LIMIT 1")
        MembershipLevel selectLevelByPoints(@Param("points") Integer points);

        /**
         * 根据累计消费金额查询对应等级（min_points字段复用为消费阈值）
         */
        @Select("SELECT * FROM membership_level WHERE min_points <= #{expense} ORDER BY min_points DESC LIMIT 1")
        MembershipLevel selectLevelByExpense(@Param("expense") BigDecimal expense);

        /**
         * 查询下一个等级（根据当前消费金额）
         */
        @Select("SELECT * FROM membership_level WHERE min_points > #{expense} ORDER BY min_points ASC LIMIT 1")
        MembershipLevel selectNextLevel(@Param("expense") BigDecimal expense);

        /**
         * 查询用户会员信息
         */
        @Select("SELECT * FROM user_membership WHERE user_id = #{userId}")
        UserMembership selectByUserId(@Param("userId") Long userId);

        /**
         * 插入用户会员信息
         */
        @Insert("INSERT INTO user_membership (user_id, level_id, points, total_expense, updated_at) " +
                        "VALUES (#{userId}, #{levelId}, #{points}, #{totalExpense}, NOW())")
        int insert(UserMembership membership);

        /**
         * 更新用户会员信息
         */
        @Update("UPDATE user_membership SET level_id = #{levelId}, points = #{points}, total_expense = #{totalExpense}, updated_at = NOW() "
                        +
                        "WHERE user_id = #{userId}")
        int update(UserMembership membership);

        /**
         * 增加积分
         */
        @Update("UPDATE user_membership SET points = points + #{points}, updated_at = NOW() WHERE user_id = #{userId}")
        int addPoints(@Param("userId") Long userId, @Param("points") Integer points);

        /**
         * 增加累计消费金额
         */
        @Update("UPDATE user_membership SET total_expense = total_expense + #{amount}, updated_at = NOW() WHERE user_id = #{userId}")
        int addExpense(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
