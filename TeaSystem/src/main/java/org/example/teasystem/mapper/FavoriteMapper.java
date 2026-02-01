package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.UserFavorite;
import org.example.teasystem.entity.UserFootprint;

import java.util.List;

/**
 * 用户收藏和足迹Mapper
 */
@Mapper
public interface FavoriteMapper {

    // ==================== 收藏 ====================

    /**
     * 查询用户收藏列表
     */
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<UserFavorite> selectFavorites(@Param("userId") Long userId, @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计用户收藏数量
     */
    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId}")
    long countFavorites(@Param("userId") Long userId);

    /**
     * 检查是否已收藏
     */
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    UserFavorite selectFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 添加收藏
     */
    @Insert("INSERT INTO user_favorite (user_id, product_id, created_at) VALUES (#{userId}, #{productId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFavorite(UserFavorite favorite);

    /**
     * 取消收藏
     */
    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    // ==================== 足迹 ====================

    /**
     * 查询用户足迹列表
     */
    @Select("SELECT * FROM user_footprint WHERE user_id = #{userId} ORDER BY viewed_at DESC LIMIT #{offset}, #{pageSize}")
    List<UserFootprint> selectFootprints(@Param("userId") Long userId, @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计用户足迹数量
     */
    @Select("SELECT COUNT(*) FROM user_footprint WHERE user_id = #{userId}")
    long countFootprints(@Param("userId") Long userId);

    /**
     * 添加/更新足迹
     */
    @Insert("INSERT INTO user_footprint (user_id, product_id, viewed_at) VALUES (#{userId}, #{productId}, NOW()) " +
            "ON DUPLICATE KEY UPDATE viewed_at = NOW()")
    int upsertFootprint(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 删除足迹
     */
    @Delete("DELETE FROM user_footprint WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteFootprint(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 清空用户足迹
     */
    @Delete("DELETE FROM user_footprint WHERE user_id = #{userId}")
    int clearFootprints(@Param("userId") Long userId);
}
