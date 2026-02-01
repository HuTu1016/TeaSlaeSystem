package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.UserEvent;

import java.util.List;

/**
 * 用户行为事件Mapper
 */
@Mapper
public interface UserEventMapper {

        /**
         * 插入事件
         */
        @Insert("INSERT INTO user_event (user_id, device_id, event_type, product_id, category_id, keyword, event_data, created_at) "
                        +
                        "VALUES (#{userId}, #{deviceId}, #{eventType}, #{productId}, #{categoryId}, #{keyword}, #{eventData}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(UserEvent event);

        /**
         * 查询用户最近浏览的商品ID
         */
        @Select("SELECT DISTINCT product_id FROM user_event " +
                        "WHERE (user_id = #{userId} OR device_id = #{deviceId}) " +
                        "AND event_type = 'VIEW' AND product_id IS NOT NULL " +
                        "ORDER BY created_at DESC LIMIT #{limit}")
        List<Long> selectRecentViewedProducts(@Param("userId") Long userId, @Param("deviceId") String deviceId,
                        @Param("limit") int limit);

        /**
         * 查询用户最近浏览的类目ID
         */
        @Select("SELECT DISTINCT category_id FROM user_event " +
                        "WHERE (user_id = #{userId} OR device_id = #{deviceId}) " +
                        "AND event_type = 'VIEW' AND category_id IS NOT NULL " +
                        "ORDER BY created_at DESC LIMIT #{limit}")
        List<Long> selectRecentViewedCategories(@Param("userId") Long userId, @Param("deviceId") String deviceId,
                        @Param("limit") int limit);

        /**
         * 查询热门商品（按浏览量）
         */
        @Select("SELECT product_id, COUNT(*) as cnt FROM user_event " +
                        "WHERE event_type = 'VIEW' AND product_id IS NOT NULL " +
                        "AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY product_id ORDER BY cnt DESC LIMIT #{limit}")
        List<Long> selectHotProducts(@Param("limit") int limit);

        /**
         * 查询热门搜索词
         */
        @Select("SELECT keyword, COUNT(*) as cnt FROM user_event " +
                        "WHERE event_type = 'SEARCH' AND keyword IS NOT NULL " +
                        "AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY keyword ORDER BY cnt DESC LIMIT #{limit}")
        List<String> selectHotKeywords(@Param("limit") int limit);

        /**
         * 查询热门搜索词（带计数）
         */
        @Select("SELECT keyword FROM user_event " +
                        "WHERE event_type = 'SEARCH' AND keyword IS NOT NULL AND keyword != '' " +
                        "AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "GROUP BY keyword ORDER BY COUNT(*) DESC LIMIT #{limit}")
        List<String> selectHotKeywordsOnly(@Param("limit") int limit);
}
