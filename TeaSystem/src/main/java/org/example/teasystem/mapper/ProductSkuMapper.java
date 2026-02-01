package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.ProductSku;

import java.util.List;

/**
 * 商品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper {
    
    /**
     * 根据ID查询SKU
     */
    @Select("SELECT * FROM product_sku WHERE id = #{id}")
    ProductSku findById(@Param("id") Long id);
    
    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} ORDER BY id ASC")
    List<ProductSku> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据商品ID查询启用的SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} AND status = 1 ORDER BY id ASC")
    List<ProductSku> findEnabledByProductId(@Param("productId") Long productId);
    
    /**
     * 插入SKU
     */
    @Insert("INSERT INTO product_sku (product_id, sku_name, price, stock, status, created_at) " +
            "VALUES (#{productId}, #{skuName}, #{price}, #{stock}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSku sku);
    
    /**
     * 更新SKU
     */
    @Update("UPDATE product_sku SET sku_name = #{skuName}, price = #{price}, stock = #{stock}, " +
            "status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(ProductSku sku);
    
    /**
     * 扣减库存
     */
    @Update("UPDATE product_sku SET stock = stock - #{quantity}, updated_at = NOW() WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
    
    /**
     * 增加库存
     */
    @Update("UPDATE product_sku SET stock = stock + #{quantity}, updated_at = NOW() WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);
    
    /**
     * 删除SKU
     */
    @Delete("DELETE FROM product_sku WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    // ==================== 新增方法 ====================

    /**
     * 根据ID查询SKU（别名）
     */
    @Select("SELECT * FROM product_sku WHERE id = #{id}")
    ProductSku selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} ORDER BY id ASC")
    List<ProductSku> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据商品ID删除SKU
     */
    @Delete("DELETE FROM product_sku WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
}
