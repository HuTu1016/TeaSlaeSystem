package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Category;

import java.util.List;

/**
 * 分类Mapper
 */
@Mapper
public interface CategoryMapper {

    /**
     * 根据ID查询分类
     */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(@Param("id") Long id);

    /**
     * 查询所有分类
     */
    @Select("SELECT * FROM category ORDER BY sort ASC, id ASC")
    List<Category> findAll();

    /**
     * 查询所有启用的分类
     */
    @Select("SELECT * FROM category WHERE status = 1 ORDER BY sort ASC, id ASC")
    List<Category> findAllEnabled();

    /**
     * 查询子分类
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort ASC")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    /**
     * 查询顶级分类
     */
    @Select("SELECT * FROM category WHERE parent_id = 0 AND status = 1 ORDER BY sort ASC")
    List<Category> findTopCategories();

    /**
     * 插入分类
     */
    @Insert("INSERT INTO category (parent_id, name, icon, sort, status, created_at) " +
            "VALUES (#{parentId}, #{name}, #{icon}, #{sort}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    /**
     * 更新分类
     */
    @Update("UPDATE category SET name = #{name}, icon = #{icon}, sort = #{sort}, status = #{status} WHERE id = #{id}")
    int update(Category category);

    /**
     * 更新分类状态
     */
    @Update("UPDATE category SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询指定分类的所有子分类ID（直接子分类）
     * 注意：仅查询直接子分类，用于在Service层递归调用
     */
    @Select("SELECT id FROM category WHERE parent_id = #{parentId} AND status = 1")
    List<Long> findChildIds(@Param("parentId") Long parentId);
}
