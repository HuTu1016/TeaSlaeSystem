package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Banner;

import java.util.List;

/**
 * Banner Mapper
 */
@Mapper
public interface BannerMapper {

    /**
     * 根据ID查询Banner
     */
    @Select("SELECT * FROM banner WHERE id = #{id}")
    Banner selectById(@Param("id") Long id);

    /**
     * 查询启用的Banner列表（按排序）
     */
    @Select("SELECT * FROM banner WHERE status = 1 ORDER BY sort ASC, id DESC")
    List<Banner> selectEnabled();

    /**
     * 分页查询Banner（管理端）
     */
    @Select("<script>" +
            "SELECT * FROM banner WHERE 1=1 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            " ORDER BY sort ASC, id DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Banner> selectList(@Param("status") Integer status, @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计Banner数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM banner WHERE 1=1 " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    long count(@Param("status") Integer status);

    /**
     * 插入Banner
     */
    @Insert("INSERT INTO banner (title, image_url, link_type, link_value, sort, status, created_at, updated_at) " +
            "VALUES (#{title}, #{imageUrl}, #{linkType}, #{linkValue}, #{sort}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    /**
     * 更新Banner
     */
    @Update("UPDATE banner SET title = #{title}, image_url = #{imageUrl}, link_type = #{linkType}, " +
            "link_value = #{linkValue}, sort = #{sort}, updated_at = NOW() WHERE id = #{id}")
    int update(Banner banner);

    /**
     * 更新状态
     */
    @Update("UPDATE banner SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 删除Banner
     */
    @Delete("DELETE FROM banner WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
