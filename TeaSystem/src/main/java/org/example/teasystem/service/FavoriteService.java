package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Product;

/**
 * 收藏和足迹服务接口
 */
public interface FavoriteService {

    // ==================== 收藏 ====================

    /**
     * 获取用户收藏列表
     */
    PageResult<Product> getFavorites(Long userId, Integer page, Integer pageSize);

    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long productId);

    // ==================== 足迹 ====================

    /**
     * 获取用户足迹列表
     */
    PageResult<Product> getFootprints(Long userId, Integer page, Integer pageSize);

    /**
     * 记录足迹
     */
    void addFootprint(Long userId, Long productId);

    /**
     * 删除足迹
     */
    void removeFootprint(Long userId, Long productId);

    /**
     * 清空足迹
     */
    void clearFootprints(Long userId);
}
