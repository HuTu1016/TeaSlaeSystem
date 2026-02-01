package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.ContentArticle;
import org.example.teasystem.mapper.ContentMapper;
import org.example.teasystem.service.ContentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内容文章服务实现
 */
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentMapper contentMapper;

    @Override
    public PageResult<ContentArticle> getPublishedList(String type, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<ContentArticle> list = contentMapper.selectPublishedList(type, offset, pageSize);
        long total = contentMapper.countPublished(type);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public ContentArticle getDetail(Long id) {
        ContentArticle article = contentMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 仅允许查看已发布的文章
        if (!"PUBLISHED".equals(article.getStatus())) {
            throw new BusinessException(404, "文章不存在");
        }
        // 增加浏览量
        contentMapper.incrementViewCount(id);
        return article;
    }

    @Override
    public PageResult<ContentArticle> getList(String type, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<ContentArticle> list = contentMapper.selectList(type, status, offset, pageSize);
        long total = contentMapper.count(type, status);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public ContentArticle getArticleById(Long id) {
        ContentArticle article = contentMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        return article;
    }

    @Override
    public Long createArticle(ContentArticle article) {
        article.setStatus("DRAFT");
        article.setViewCount(0);
        contentMapper.insert(article);
        return article.getId();
    }

    @Override
    public void updateArticle(ContentArticle article) {
        ContentArticle existing = contentMapper.selectById(article.getId());
        if (existing == null) {
            throw new BusinessException(404, "文章不存在");
        }
        contentMapper.update(article);
    }

    @Override
    public void publishArticle(Long id) {
        ContentArticle article = contentMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        contentMapper.updateStatus(id, "PUBLISHED");
    }

    @Override
    public void unpublishArticle(Long id) {
        ContentArticle article = contentMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        contentMapper.updateStatus(id, "DRAFT");
    }

    @Override
    public void deleteArticle(Long id) {
        ContentArticle article = contentMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        contentMapper.deleteById(id);
    }
}
