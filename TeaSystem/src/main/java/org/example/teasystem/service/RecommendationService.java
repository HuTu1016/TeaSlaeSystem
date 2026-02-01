package org.example.teasystem.service;

import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.UserEvent;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {

    /**
     * 获取首页推荐商品
     */
    List<Product> getHomeRecommendations(Long userId, String deviceId, int limit);

    /**
     * 获取热门商品
     */
    List<Product> getHotProducts(int limit);

    /**
     * 获取新品
     */
    List<Product> getNewProducts(int limit);

    /**
     * 获取相似商品
     */
    List<Product> getSimilarProducts(Long productId, int limit);

    /**
     * 获取猜你喜欢
     */
    List<Product> getGuessYouLike(Long userId, String deviceId, int limit);

    /**
     * 获取热门搜索词
     */
    List<String> getHotKeywords(int limit);

    /**
     * 上报用户行为
     */
    void trackEvent(UserEvent event);
}
