package org.example.teasystem.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.common.constant.RedisConstants;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.dto.product.ProductListVO;
import org.example.teasystem.dto.product.ProductQueryRequest;
import org.example.teasystem.entity.Product;
import org.example.teasystem.mapper.CategoryMapper;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.service.ProductCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品缓存服务实现
 * 使用 Cache-Aside 模式：先查缓存，未命中则查库并回写
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl implements ProductCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<ProductListVO> getHotProductsCached(int limit) {
        String cacheKey = RedisConstants.HOT_PRODUCTS + ":" + limit;

        // 1. 尝试从缓存获取
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                log.debug("热门商品缓存命中: limit={}", limit);
                return objectMapper.readValue(cachedJson, new TypeReference<List<ProductListVO>>() {
                });
            }
        } catch (Exception e) {
            log.warn("读取热门商品缓存失败", e);
        }

        // 2. 缓存未命中，查询数据库
        log.debug("热门商品缓存未命中，查询数据库: limit={}", limit);
        List<Product> products = productMapper.selectHot(limit);
        List<ProductListVO> result = products.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        // 3. 回写缓存（24小时过期）
        try {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(cacheKey, json, RedisConstants.CACHE_24H_SECONDS, TimeUnit.SECONDS);
            log.debug("热门商品已缓存: limit={}", limit);
        } catch (Exception e) {
            log.warn("缓存热门商品失败", e);
        }

        return result;
    }

    @Override
    public List<ProductListVO> getNewProductsCached(int limit) {
        String cacheKey = RedisConstants.NEW_PRODUCTS + ":" + limit;

        // 1. 尝试从缓存获取
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                log.debug("新品缓存命中: limit={}", limit);
                return objectMapper.readValue(cachedJson, new TypeReference<List<ProductListVO>>() {
                });
            }
        } catch (Exception e) {
            log.warn("读取新品缓存失败", e);
        }

        // 2. 缓存未命中，查询数据库
        log.debug("新品缓存未命中，查询数据库: limit={}", limit);
        List<Product> products = productMapper.selectNew(limit);
        List<ProductListVO> result = products.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        // 3. 回写缓存（24小时过期）
        try {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(cacheKey, json, RedisConstants.CACHE_24H_SECONDS, TimeUnit.SECONDS);
            log.debug("新品已缓存: limit={}", limit);
        } catch (Exception e) {
            log.warn("缓存新品失败", e);
        }

        return result;
    }

    @Override
    public PageResult<ProductListVO> getProductListCached(ProductQueryRequest request) {
        // 生成缓存Key（基于查询条件的哈希值）
        String cacheKey = RedisConstants.PRODUCT_LIST + generateQueryHash(request);

        // 1. 尝试从缓存获取
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                log.debug("商品列表缓存命中: key={}", cacheKey);
                return objectMapper.readValue(cachedJson, new TypeReference<PageResult<ProductListVO>>() {
                });
            }
        } catch (Exception e) {
            log.warn("读取商品列表缓存失败", e);
        }

        // 2. 缓存未命中，查询数据库
        log.debug("商品列表缓存未命中，查询数据库");
        int offset = (request.getPage() - 1) * request.getPageSize();

        List<Long> categoryIds = getAllCategoryIdsIncludingChildren(request.getCategoryId());

        List<Product> products = productMapper.selectList(
                categoryIds,
                request.getKeyword(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getSort(),
                offset,
                request.getPageSize());

        long total = productMapper.countByQuery(
                categoryIds,
                request.getKeyword(),
                request.getMinPrice(),
                request.getMaxPrice());

        List<ProductListVO> voList = products.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        PageResult<ProductListVO> result = PageResult.of(request.getPage(), request.getPageSize(), total, voList);

        // 3. 回写缓存（1小时过期）
        try {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(cacheKey, json, RedisConstants.CACHE_1H_SECONDS, TimeUnit.SECONDS);
            log.debug("商品列表已缓存: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("缓存商品列表失败", e);
        }

        return result;
    }

    @Override
    public void evictHotProductsCache() {
        try {
            Set<String> keys = redisTemplate.keys(RedisConstants.HOT_PRODUCTS + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已清除热门商品缓存: {} 个", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除热门商品缓存失败", e);
        }
    }

    @Override
    public void evictNewProductsCache() {
        try {
            Set<String> keys = redisTemplate.keys(RedisConstants.NEW_PRODUCTS + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已清除新品缓存: {} 个", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除新品缓存失败", e);
        }
    }

    @Override
    public void evictProductListCache() {
        try {
            Set<String> keys = redisTemplate.keys(RedisConstants.PRODUCT_LIST + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("已清除商品列表缓存: {} 个", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除商品列表缓存失败", e);
        }
    }

    @Override
    public void evictAllProductCache() {
        evictHotProductsCache();
        evictNewProductsCache();
        evictProductListCache();
        log.info("已清除所有商品相关缓存");
    }

    /**
     * 根据查询条件生成哈希值作为缓存Key的一部分
     */
    private String generateQueryHash(ProductQueryRequest request) {
        String queryString = String.format("%d:%s:%s:%s:%s:%d:%d",
                request.getCategoryId(),
                request.getKeyword(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getSort(),
                request.getPage(),
                request.getPageSize());
        return DigestUtils.md5DigestAsHex(queryString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取指定分类ID及其所有子分类ID的列表
     */
    private List<Long> getAllCategoryIdsIncludingChildren(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        result.add(categoryId);
        collectChildCategoryIds(categoryId, result);
        return result;
    }

    /**
     * 递归收集所有子分类ID
     */
    private void collectChildCategoryIds(Long parentId, List<Long> result) {
        List<Long> childIds = categoryMapper.findChildIds(parentId);
        if (childIds != null && !childIds.isEmpty()) {
            for (Long childId : childIds) {
                result.add(childId);
                collectChildCategoryIds(childId, result);
            }
        }
    }

    /**
     * 转换为商品列表VO
     */
    private ProductListVO convertToListVO(Product product) {
        ProductListVO vo = new ProductListVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setSubtitle(product.getSubtitle());
        vo.setOrigin(product.getOrigin());
        vo.setProcess(product.getProcess());
        vo.setTaste(product.getTaste());
        vo.setMainImage(product.getMainImage());
        vo.setMinPrice(product.getMinPrice());
        vo.setMaxPrice(product.getMaxPrice());
        vo.setSalesCount(product.getSalesCount());
        vo.setStatus(product.getStatus());
        return vo;
    }
}
