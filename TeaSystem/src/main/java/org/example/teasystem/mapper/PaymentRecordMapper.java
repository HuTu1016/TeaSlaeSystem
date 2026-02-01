package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.PaymentRecord;

import java.time.LocalDateTime;

/**
 * 支付记录Mapper
 */
@Mapper
public interface PaymentRecordMapper {
    
    /**
     * 根据ID查询支付记录
     */
    @Select("SELECT * FROM payment_record WHERE id = #{id}")
    PaymentRecord findById(@Param("id") Long id);
    
    /**
     * 根据支付单号查询支付记录
     */
    @Select("SELECT * FROM payment_record WHERE pay_no = #{payNo}")
    PaymentRecord findByPayNo(@Param("payNo") String payNo);
    
    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM payment_record WHERE order_id = #{orderId} ORDER BY created_at DESC LIMIT 1")
    PaymentRecord findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 插入支付记录
     */
    @Insert("INSERT INTO payment_record (pay_no, order_id, channel, status, amount, created_at) " +
            "VALUES (#{payNo}, #{orderId}, #{channel}, #{status}, #{amount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentRecord record);
    
    /**
     * 更新支付状态
     */
    @Update("UPDATE payment_record SET status = #{status}, paid_at = #{paidAt}, " +
            "notify_raw = #{notifyRaw}, notify_at = #{notifyAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, 
                     @Param("paidAt") LocalDateTime paidAt, @Param("notifyRaw") String notifyRaw,
                     @Param("notifyAt") LocalDateTime notifyAt);
    
    /**
     * 更新为退款状态
     */
    @Update("UPDATE payment_record SET status = 'REFUNDED' WHERE id = #{id}")
    int updateRefunded(@Param("id") Long id);

    // ==================== 新增方法 ====================

    /**
     * 根据支付单号查询
     */
    @Select("SELECT * FROM payment_record WHERE payment_no = #{paymentNo}")
    PaymentRecord selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM payment_record WHERE order_id = #{orderId} ORDER BY created_at DESC LIMIT 1")
    PaymentRecord selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单ID查询待支付记录
     */
    @Select("SELECT * FROM payment_record WHERE order_id = #{orderId} AND status = 0 ORDER BY created_at DESC LIMIT 1")
    PaymentRecord selectPendingByOrderId(@Param("orderId") Long orderId);

    /**
     * 更新支付记录
     */
    @Update("<script>" +
            "UPDATE payment_record SET updated_at = NOW() " +
            "<if test='status != null'>, status = #{status}</if>" +
            "<if test='thirdPartyNo != null'>, third_party_no = #{thirdPartyNo}</if>" +
            "<if test='paidAt != null'>, paid_at = #{paidAt}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(PaymentRecord record);
}
