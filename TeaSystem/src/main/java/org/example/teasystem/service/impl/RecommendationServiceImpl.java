package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.teasystem.common.constant.RedisConstants;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.UserEvent;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.UserEventMapper;
import org.example.teasystem.service.RecommendationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductMapper productMapper;
    private final UserEventMapper userEventMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String HOT_PRODUCTS_KEY = "recommendation:hot_products";
    private static final String HOT_KEYWORDS_KEY = "recommendation:hot_keywords";

    @Override
    public List<Product> getHomeRecommendations(Long userId, String deviceId, int limit) {
        // 首页推荐：优先个性化推荐，不足则用热门商品补充
        List<Product> result = new ArrayList<>();

        // 1. 获取用户最近浏览的类目
        List<Long> recentCategories = userEventMapper.selectRecentViewedCategories(userId, deviceId, 3);

        // 2. 从这些类目中获取热门商品
        if (!recentCategories.isEmpty()) {
            for (Long categoryId : recentCategories) {
                List<Product> categoryProducts = productMapper.selectHotByCategory(categoryId, limit / 3);
                result.addAll(categoryProducts);
                if (result.size() >= limit)
                    break;
            }
        }

        // 3. 不足则用全局热门补充
        if (result.size() < limit) {
            List<Product> hotProducts = getHotProducts(limit - result.size());
            result.addAll(hotProducts);
        }

        return result.subList(0, Math.min(result.size(), limit));
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        // 使用与 ProductCacheService 相同的缓存 Key
        String cacheKey = RedisConstants.HOT_PRODUCTS + ":entity:" + limit;

        // 尝试从缓存获取
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                log.debug("热门商品实体缓存命中: limit={}", limit);
                return objectMapper.readValue(cachedJson, new TypeReference<List<Product>>() {
                });
            }
        } catch (Exception e) {
            log.warn("读取热门商品实体缓存失败", e);
        }

        // 缓存未命中，查询数据库
        log.debug("热门商品实体缓存未命中，查询数据库: limit={}", limit);
        List<Product> products = productMapper.selectHot(limit);

        // 回写缓存（24小时过期）
        try {
            String json = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(cacheKey, json, RedisConstants.CACHE_24H_SECONDS, TimeUnit.SECONDS);
            log.debug("热门商品实体已缓存: limit={}", limit);
        } catch (Exception e) {
            log.warn("缓存热门商品实体失败", e);
        }

        return products;
    }

    @Override
    public List<Product> getNewProducts(int limit) {
        // 使用与 ProductCacheService 相同的缓存 Key
        String cacheKey = RedisConstants.NEW_PRODUCTS + ":entity:" + limit;

        // 尝试从缓存获取
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                log.debug("新品实体缓存命中: limit={}", limit);
                return objectMapper.readValue(cachedJson, new TypeReference<List<Product>>() {
                });
            }
        } catch (Exception e) {
            log.warn("读取新品实体缓存失败", e);
        }

        // 缓存未命中，查询数据库
        log.debug("新品实体缓存未命中，查询数据库: limit={}", limit);
        List<Product> products = productMapper.selectNew(limit);

        // 回写缓存（24小时过期）
        try {
            String json = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(cacheKey, json, RedisConstants.CACHE_24H_SECONDS, TimeUnit.SECONDS);
            log.debug("新品实体已缓存: limit={}", limit);
        } catch (Exception e) {
            log.warn("缓存新品实体失败", e);
        }

        return products;
    }

    @Override
    public List<Product> getSimilarProducts(Long productId, int limit) {
        // 获取商品的类目，推荐同类目的其他商品
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return new ArrayList<>();
        }
        return productMapper.selectSimilar(product.getCategoryId(), productId, limit);
    }

    @Override
    public List<Product> getGuessYouLike(Long userId, String deviceId, int limit) {
        // 基于用户最近浏览的商品推荐相似商品
        List<Long> recentProducts = userEventMapper.selectRecentViewedProducts(userId, deviceId, 5);
        if (recentProducts.isEmpty()) {
            return getHotProducts(limit);
        }

        List<Product> result = new ArrayList<>();
        for (Long productId : recentProducts) {
            List<Product> similar = getSimilarProducts(productId, 2);
            result.addAll(similar);
            if (result.size() >= limit)
                break;
        }

        return result.subList(0, Math.min(result.size(), limit));
    }

    @Override
    public List<String> getHotKeywords(int limit) {
        return userEventMapper.selectHotKeywords(limit);
    }

    @Override
    public void trackEvent(UserEvent event) {
        userEventMapper.insert(event);
    }
}
