package org.example.teasystem.service;

import org.example.teasystem.entity.BalanceLog;
import org.example.teasystem.entity.UserBalance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 余额服务接口
 */
public interface BalanceService {

    /**
     * 获取用户余额信息
     */
    UserBalance getBalance(Long userId);

    /**
     * 初始化用户余额（新用户注册时调用）
     */
    UserBalance initBalance(Long userId);

    /**
     * 增加余额（收入）
     * @param userId 用户ID
     * @param amount 金额
     * @param type 交易类型
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @param remark 备注
     * @return 变动记录
     */
    BalanceLog increaseBalance(Long userId, BigDecimal amount, String type, String bizType, String bizId, String remark);

    /**
     * 减少余额（支出）
     * @param userId 用户ID
     * @param amount 金额
     * @param type 交易类型
     * @param bizType 业务类型
     * @param bizId 业务ID
     * @param remark 备注
     * @return 变动记录
     */
    BalanceLog decreaseBalance(Long userId, BigDecimal amount, String type, String bizType, String bizId, String remark);

    /**
     * 分页查询余额明细
     */
    Map<String, Object> getBalanceLogs(Long userId, String type, int page, int size);

    /**
     * 获取余额明细列表
     */
    List<BalanceLog> getBalanceLogList(Long userId, String type, int page, int size);

    /**
     * 统计余额明细数量
     */
    int countBalanceLogs(Long userId, String type);
}
