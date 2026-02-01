package org.example.teasystem.common.util;

import cn.hutool.core.util.IdUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单号生成工具
 */
public class OrderNoGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * 生成订单号
     * 格式: T + yyyyMMddHHmmss + 6位随机数
     */
    public static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "T" + timestamp + random;
    }
    
    /**
     * 生成支付单号
     * 格式: P + yyyyMMddHHmmss + 6位随机数
     */
    public static String generatePayNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "P" + timestamp + random;
    }
    
    /**
     * 生成售后单号
     * 格式: AS + yyyyMMddHHmmss + 6位随机数
     */
    public static String generateAfterSaleNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return "AS" + timestamp + random;
    }
    
    /**
     * 生成溯源码
     * 格式: TRC + yyyyMMddHHmmss + 4位随机数
     */
    public static String generateTraceCode() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "TRC" + timestamp + random;
    }
    
    /**
     * 生成UUID
     */
    public static String generateUUID() {
        return IdUtil.fastSimpleUUID();
    }
}
