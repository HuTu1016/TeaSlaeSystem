package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为事件实体（用于推荐）
 */
@Data
public class UserEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件ID
     */
    private Long id;

    /**
     * 用户ID（匿名用户为null）
     */
    private Long userId;

    /**
     * 设备ID（匿名用户标识）
     */
    private String deviceId;

    /**
     * 事件类型：VIEW-浏览, CART-加购, ORDER-下单, SEARCH-搜索
     */
    private String eventType;

    /**
     * 商品ID（浏览/加购/下单事件）
     */
    private Long productId;

    /**
     * 类目ID
     */
    private Long categoryId;

    /**
     * 搜索关键词（搜索事件）
     */
    private String keyword;

    /**
     * 事件数据（JSON扩展）
     */
    private String eventData;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
