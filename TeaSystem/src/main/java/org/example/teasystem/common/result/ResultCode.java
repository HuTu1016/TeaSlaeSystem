package org.example.teasystem.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    
    // 成功
    SUCCESS(0, "OK"),
    
    // 通用错误 400xx
    FAIL(40000, "操作失败"),
    PARAM_ERROR(40001, "参数错误"),
    PARAM_INVALID(40002, "参数不合法"),
    AMOUNT_MISMATCH(40003, "金额不一致"),
    
    // 认证错误 401xx
    UNAUTHORIZED(40101, "未登录或Token已过期"),
    TOKEN_INVALID(40102, "Token无效"),
    TOKEN_EXPIRED(40103, "Token已过期"),
    
    // 权限错误 403xx
    FORBIDDEN(40301, "无权限访问"),
    ACCOUNT_DISABLED(40302, "账号已被禁用"),
    
    // 资源不存在 404xx
    NOT_FOUND(40401, "资源不存在"),
    USER_NOT_FOUND(40402, "用户不存在"),
    PRODUCT_NOT_FOUND(40403, "商品不存在"),
    ORDER_NOT_FOUND(40404, "订单不存在"),
    
    // 冲突错误 409xx
    STOCK_NOT_ENOUGH(40901, "库存不足"),
    PHONE_EXISTS(40902, "手机号已存在"),
    USERNAME_EXISTS(40903, "用户名已存在"),
    DUPLICATE_SUBMIT(40904, "重复提交"),
    AFTER_SALE_EXISTS(40905, "该订单项已存在未完结的售后"),
    ILLEGAL_STATUS(40906, "状态不合法"),
    
    // 请求过多 429xx
    TOO_MANY_REQUESTS(42901, "请求过于频繁，请稍后重试"),
    ACCOUNT_LOCKED(42902, "账号已被锁定，请10分钟后重试"),
    
    // 服务器错误 500xx
    SERVER_ERROR(50000, "系统繁忙，请稍后重试"),
    DATABASE_ERROR(50001, "数据库错误"),
    REDIS_ERROR(50002, "缓存服务错误");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
