package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Address;

import java.util.List;

/**
 * 地址Mapper
 */
@Mapper
public interface AddressMapper {
    
    /**
     * 根据ID查询地址
     */
    @Select("SELECT * FROM address WHERE id = #{id}")
    Address findById(@Param("id") Long id);
    
    /**
     * 查询用户的所有地址
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<Address> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的默认地址
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address findDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 插入地址
     */
    @Insert("INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail, is_default, created_at) " +
            "VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);
    
    /**
     * 更新地址
     */
    @Update("UPDATE address SET receiver_name = #{receiverName}, receiver_phone = #{receiverPhone}, " +
            "province = #{province}, city = #{city}, district = #{district}, detail = #{detail}, " +
            "is_default = #{isDefault}, updated_at = NOW() WHERE id = #{id}")
    int update(Address address);
    
    /**
     * 删除地址
     */
    @Delete("DELETE FROM address WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 清除用户所有默认地址标记
     */
    @Update("UPDATE address SET is_default = 0, updated_at = NOW() WHERE user_id = #{userId}")
    int clearDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 设置默认地址
     */
    @Update("UPDATE address SET is_default = 1, updated_at = NOW() WHERE id = #{id}")
    int setDefault(@Param("id") Long id);
}
