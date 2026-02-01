package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Merchant;

import java.util.List;

/**
 * 商家Mapper
 */
@Mapper
public interface MerchantMapper {

        /**
         * 根据ID查询商家
         */
        @Select("SELECT * FROM merchant WHERE id = #{id}")
        Merchant findById(@Param("id") Long id);

        /**
         * 根据用户ID查询商家
         */
        @Select("SELECT * FROM merchant WHERE user_id = #{userId}")
        Merchant findByUserId(@Param("userId") Long userId);

        /**
         * 插入商家
         */
        @Insert("INSERT INTO merchant (user_id, shop_name, shop_desc, shop_logo, contact_phone, business_license, status, created_at) "
                        +
                        "VALUES (#{userId}, #{shopName}, #{shopDesc}, #{shopLogo}, #{contactPhone}, #{businessLicense}, #{status}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Merchant merchant);

        /**
         * 更新商家信息
         */
        @Update("UPDATE merchant SET shop_name = #{shopName}, shop_desc = #{shopDesc}, shop_logo = #{shopLogo}, " +
                        "contact_phone = #{contactPhone}, updated_at = NOW() WHERE id = #{id}")
        int update(Merchant merchant);

        /**
         * 更新商家状态
         */
        @Update("UPDATE merchant SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") Integer status);

        /**
         * 根据ID查询商家（别名）
         */
        @Select("SELECT * FROM merchant WHERE id = #{id}")
        Merchant selectById(@Param("id") Long id);

        /**
         * 分页查询商家列表（支持通过店铺名称、商家姓名模糊搜索）
         */
        @Select("<script>" +
                        "SELECT m.* FROM merchant m " +
                        "LEFT JOIN user u ON m.user_id = u.id " +
                        "WHERE 1=1 " +
                        "<if test='status != null'>AND m.status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (m.shop_name LIKE CONCAT('%',#{keyword},'%') OR u.nickname LIKE CONCAT('%',#{keyword},'%') OR u.username LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        " ORDER BY m.created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Merchant> selectList(@Param("keyword") String keyword, @Param("status") Integer status,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计商家数量（支持通过店铺名称、商家姓名模糊搜索）
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM merchant m " +
                        "LEFT JOIN user u ON m.user_id = u.id " +
                        "WHERE 1=1 " +
                        "<if test='status != null'>AND m.status = #{status}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (m.shop_name LIKE CONCAT('%',#{keyword},'%') OR u.nickname LIKE CONCAT('%',#{keyword},'%') OR u.username LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "</script>")
        int count(@Param("keyword") String keyword, @Param("status") Integer status);

        /**
         * 统计总商家数
         */
        @Select("SELECT COUNT(*) FROM merchant")
        long countAll();
}
