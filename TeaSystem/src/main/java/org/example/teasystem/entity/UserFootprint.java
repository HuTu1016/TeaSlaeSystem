package org.example.teasystem.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户浏览足迹实体
 */
@Data
public class UserFootprint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 足迹ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 浏览时间
     */
    private LocalDateTime viewedAt;
}
