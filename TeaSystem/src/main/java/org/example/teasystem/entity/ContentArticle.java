package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容文章实体
 */
@Data
public class ContentArticle implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 文章类型：CULTURE/BREW_TUTORIAL
     */
    private String type;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 标签JSON
     */
    private String tagsJson;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态：DRAFT/PUBLISHED
     */
    private String status;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
