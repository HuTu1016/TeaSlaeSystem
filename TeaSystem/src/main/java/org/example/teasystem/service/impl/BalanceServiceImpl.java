package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.entity.BalanceLog;
import org.example.teasystem.entity.UserBalance;
import org.example.teasystem.mapper.BalanceMapper;
import org.example.teasystem.service.BalanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 余额服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceMapper balanceMapper;

    @Override
    public UserBalance getBalance(Long userId) {
        UserBalance balance = balanceMapper.findByUserId(userId);
        if (balance == null) {
            // 如果不存在则初始化
            balance = initBalance(userId);
        }
        return balance;
    }

    @Override
    @Transactional
    public UserBalance initBalance(Long userId) {
        UserBalance existing = balanceMapper.findByUserId(userId);
        if (existing != null) {
            return existing;
        }
        
        UserBalance balance = new UserBalance();
        balance.setUserId(userId);
        balance.setBalance(BigDecimal.ZERO);
        balance.setFrozenBalance(BigDecimal.ZERO);
        balance.setTotalIncome(BigDecimal.ZERO);
        balance.setTotalExpense(BigDecimal.ZERO);
        balanceMapper.insertBalance(balance);
        return balance;
    }

    @Override
    @Transactional
    public BalanceLog increaseBalance(Long userId, BigDecimal amount, String type, String bizType, String bizId, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }

        // 获取当前余额
        UserBalance balance = getBalance(userId);
        BigDecimal balanceBefore = balance.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // 更新余额
        int rows = balanceMapper.increaseBalance(userId, amount);
        if (rows == 0) {
            throw new RuntimeException("余额更新失败");
        }

        // 记录变动日志
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setType(type);
        balanceLog.setAmount(amount);
        balanceLog.setBalanceBefore(balanceBefore);
        balanceLog.setBalanceAfter(balanceAfter);
        balanceLog.setStatus("SUCCESS");
        balanceLog.setBizType(bizType);
        balanceLog.setBizId(bizId);
        balanceLog.setRemark(remark);
        balanceMapper.insertLog(balanceLog);

        log.info("用户 {} 余额增加 {}, 类型: {}, 业务: {}/{}", userId, amount, type, bizType, bizId);
        return balanceLog;
    }

    @Override
    @Transactional
    public BalanceLog decreaseBalance(Long userId, BigDecimal amount, String type, String bizType, String bizId, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }

        // 获取当前余额
        UserBalance balance = getBalance(userId);
        BigDecimal balanceBefore = balance.getBalance();
        
        if (balanceBefore.compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        // 更新余额
        int rows = balanceMapper.decreaseBalance(userId, amount);
        if (rows == 0) {
            throw new RuntimeException("余额不足或更新失败");
        }

        // 记录变动日志（支出用负数表示）
        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setType(type);
        balanceLog.setAmount(amount.negate());
        balanceLog.setBalanceBefore(balanceBefore);
        balanceLog.setBalanceAfter(balanceAfter);
        balanceLog.setStatus("SUCCESS");
        balanceLog.setBizType(bizType);
        balanceLog.setBizId(bizId);
        balanceLog.setRemark(remark);
        balanceMapper.insertLog(balanceLog);

        log.info("用户 {} 余额减少 {}, 类型: {}, 业务: {}/{}", userId, amount, type, bizType, bizId);
        return balanceLog;
    }

    @Override
    public Map<String, Object> getBalanceLogs(Long userId, String type, int page, int size) {
        int offset = (page - 1) * size;
        List<BalanceLog> logs = balanceMapper.findLogsByUserId(userId, type, offset, size);
        int total = balanceMapper.countLogsByUserId(userId, type);

        Map<String, Object> result = new HashMap<>();
        result.put("list", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("pages", (total + size - 1) / size);
        return result;
    }

    @Override
    public List<BalanceLog> getBalanceLogList(Long userId, String type, int page, int size) {
        int offset = (page - 1) * size;
        return balanceMapper.findLogsByUserId(userId, type, offset, size);
    }

    @Override
    public int countBalanceLogs(Long userId, String type) {
        return balanceMapper.countLogsByUserId(userId, type);
    }
}
