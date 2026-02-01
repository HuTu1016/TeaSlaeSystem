package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.BalanceLog;
import org.example.teasystem.entity.UserBalance;

import java.math.BigDecimal;
import java.util.List;

/**
 * 余额相关Mapper
 */
@Mapper
public interface BalanceMapper {

    /**
     * 根据用户ID查询余额
     */
    @Select("SELECT * FROM user_balance WHERE user_id = #{userId}")
    UserBalance findByUserId(@Param("userId") Long userId);

    /**
     * 插入用户余额记录
     */
    @Insert("INSERT INTO user_balance (user_id, balance, frozen_balance, total_income, total_expense, created_at) " +
            "VALUES (#{userId}, #{balance}, #{frozenBalance}, #{totalIncome}, #{totalExpense}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertBalance(UserBalance userBalance);

    /**
     * 更新用户余额（增加）
     */
    @Update("UPDATE user_balance SET balance = balance + #{amount}, total_income = total_income + #{amount}, updated_at = NOW() " +
            "WHERE user_id = #{userId}")
    int increaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 更新用户余额（减少，需要检查余额是否足够）
     */
    @Update("UPDATE user_balance SET balance = balance - #{amount}, total_expense = total_expense + #{amount}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND balance >= #{amount}")
    int decreaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 冻结余额
     */
    @Update("UPDATE user_balance SET balance = balance - #{amount}, frozen_balance = frozen_balance + #{amount}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND balance >= #{amount}")
    int freezeBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 解冻余额
     */
    @Update("UPDATE user_balance SET balance = balance + #{amount}, frozen_balance = frozen_balance - #{amount}, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND frozen_balance >= #{amount}")
    int unfreezeBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 插入余额变动记录
     */
    @Insert("INSERT INTO balance_log (user_id, type, amount, balance_before, balance_after, status, biz_type, biz_id, remark, created_at) " +
            "VALUES (#{userId}, #{type}, #{amount}, #{balanceBefore}, #{balanceAfter}, #{status}, #{bizType}, #{bizId}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLog(BalanceLog log);

    /**
     * 分页查询用户余额明细
     */
    @Select("<script>" +
            "SELECT * FROM balance_log WHERE user_id = #{userId} " +
            "<if test='type != null and type != \"\"'>AND type = #{type}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
            "</script>")
    List<BalanceLog> findLogsByUserId(@Param("userId") Long userId, @Param("type") String type,
                                       @Param("offset") int offset, @Param("size") int size);

    /**
     * 统计用户余额明细数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM balance_log WHERE user_id = #{userId} " +
            "<if test='type != null and type != \"\"'>AND type = #{type}</if>" +
            "</script>")
    int countLogsByUserId(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 根据业务类型和ID查询余额记录
     */
    @Select("SELECT * FROM balance_log WHERE biz_type = #{bizType} AND biz_id = #{bizId}")
    BalanceLog findLogByBiz(@Param("bizType") String bizType, @Param("bizId") String bizId);
}
