package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.ContentArticle;

import java.util.List;

/**
 * 内容文章Mapper
 */
@Mapper
public interface ContentMapper {

        /**
         * 根据ID查询文章
         */
        @Select("SELECT * FROM content_article WHERE id = #{id}")
        ContentArticle selectById(@Param("id") Long id);

        /**
         * 分页查询文章列表（用户端，仅已发布）
         */
        @Select("<script>" +
                        "SELECT * FROM content_article WHERE status = 'PUBLISHED' " +
                        "<if test='type != null'>AND type = #{type}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<ContentArticle> selectPublishedList(@Param("type") String type,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        /**
         * 统计已发布文章数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM content_article WHERE status = 'PUBLISHED' " +
                        "<if test='type != null'>AND type = #{type}</if>" +
                        "</script>")
        long countPublished(@Param("type") String type);

        /**
         * 分页查询文章列表（管理端，可按状态筛选）
         */
        @Select("<script>" +
                        "SELECT * FROM content_article WHERE 1=1 " +
                        "<if test='type != null and type != \"\"'>AND type = #{type}</if>" +
                        "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<ContentArticle> selectList(@Param("type") String type, @Param("status") String status,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计文章数量（管理端）
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM content_article WHERE 1=1 " +
                        "<if test='type != null and type != \"\"'>AND type = #{type}</if>" +
                        "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
                        "</script>")
        long count(@Param("type") String type, @Param("status") String status);

        /**
         * 插入文章
         */
        @Insert("INSERT INTO content_article (type, title, summary, cover_url, tags_json, content, status, view_count, created_at, updated_at) "
                        +
                        "VALUES (#{type}, #{title}, #{summary}, #{coverUrl}, #{tagsJson}, #{content}, #{status}, 0, NOW(), NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(ContentArticle article);

        /**
         * 更新文章
         */
        @Update("UPDATE content_article SET type = #{type}, title = #{title}, summary = #{summary}, " +
                        "cover_url = #{coverUrl}, tags_json = #{tagsJson}, content = #{content}, updated_at = NOW() WHERE id = #{id}")
        int update(ContentArticle article);

        /**
         * 更新状态
         */
        @Update("UPDATE content_article SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 增加浏览量
         */
        @Update("UPDATE content_article SET view_count = view_count + 1 WHERE id = #{id}")
        int incrementViewCount(@Param("id") Long id);

        /**
         * 删除文章
         */
        @Delete("DELETE FROM content_article WHERE id = #{id}")
        int deleteById(@Param("id") Long id);
}
