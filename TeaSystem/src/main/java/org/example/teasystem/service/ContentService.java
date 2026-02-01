package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ContentArticle;

/**
 * 内容文章服务接口
 */
public interface ContentService {

    /**
     * 获取已发布文章列表（用户端）
     */
    PageResult<ContentArticle> getPublishedList(String type, Integer page, Integer pageSize);

    /**
     * 获取文章详情（用户端，会增加浏览量）
     */
    ContentArticle getDetail(Long id);

    /**
     * 获取文章列表（管理端）
     */
    PageResult<ContentArticle> getList(String type, String status, Integer page, Integer pageSize);

    /**
     * 根据ID获取文章（管理端，不增加浏览量）
     */
    ContentArticle getArticleById(Long id);

    /**
     * 创建文章
     */
    Long createArticle(ContentArticle article);

    /**
     * 更新文章
     */
    void updateArticle(ContentArticle article);

    /**
     * 发布文章
     */
    void publishArticle(Long id);

    /**
     * 下架文章
     */
    void unpublishArticle(Long id);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);
}
