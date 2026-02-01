package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论回复实体
 */
@Data
public class ReviewReply implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 回复ID
     */
    private Long id;
    
    /**
     * 评论ID
     */
    private Long reviewId;
    
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
