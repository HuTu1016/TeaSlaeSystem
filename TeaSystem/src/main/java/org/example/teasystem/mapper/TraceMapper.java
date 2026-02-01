package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.ProductTrace;

import java.util.List;

/**
 * 商品溯源Mapper
 */
@Mapper
public interface TraceMapper {

    /**
     * 根据ID查询溯源
     */
    @Select("SELECT * FROM product_trace WHERE id = #{id}")
    ProductTrace selectById(@Param("id") Long id);

    /**
     * 根据溯源码查询
     */
    @Select("SELECT * FROM product_trace WHERE trace_code = #{traceCode}")
    ProductTrace selectByTraceCode(@Param("traceCode") String traceCode);

    /**
     * 根据商品ID查询已审核的溯源
     */
    @Select("SELECT * FROM product_trace WHERE product_id = #{productId} AND status = 'APPROVED'")
    ProductTrace selectByProductId(@Param("productId") Long productId);

    /**
     * 分页查询商家溯源列表
     */
    @Select("<script>" +
            "SELECT * FROM product_trace WHERE merchant_id = #{merchantId} " +
            "<if test='status != null'>AND status = #{status}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ProductTrace> selectByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status,
            @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 统计商家溯源数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM product_trace WHERE merchant_id = #{merchantId} " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long countByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status);

    /**
     * 分页查询待审核溯源（管理端）
     */
    @Select("<script>" +
            "SELECT * FROM product_trace WHERE 1=1 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ProductTrace> selectList(@Param("status") String status, @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计溯源数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM product_trace WHERE 1=1 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long count(@Param("status") String status);

    /**
     * 插入溯源
     */
    @Insert("INSERT INTO product_trace (trace_code, product_id, merchant_id, batch_no, origin, pick_date, " +
            "process, producer, inspection_report_url, certificate_url, summary, status, created_at, updated_at) " +
            "VALUES (#{traceCode}, #{productId}, #{merchantId}, #{batchNo}, #{origin}, #{pickDate}, " +
            "#{process}, #{producer}, #{inspectionReportUrl}, #{certificateUrl}, #{summary}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductTrace trace);

    /**
     * 更新溯源
     */
    @Update("UPDATE product_trace SET batch_no = #{batchNo}, origin = #{origin}, pick_date = #{pickDate}, " +
            "process = #{process}, producer = #{producer}, inspection_report_url = #{inspectionReportUrl}, " +
            "certificate_url = #{certificateUrl}, summary = #{summary}, updated_at = NOW() WHERE id = #{id}")
    int update(ProductTrace trace);

    /**
     * 更新状态
     */
    @Update("UPDATE product_trace SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新审核结果
     */
    @Update("UPDATE product_trace SET status = #{status}, reject_reason = #{rejectReason}, updated_at = NOW() WHERE id = #{id}")
    int updateAudit(@Param("id") Long id, @Param("status") String status, @Param("rejectReason") String rejectReason);

    /**
     * 删除溯源
     */
    @Delete("DELETE FROM product_trace WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
