package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.UserFavorite;
import org.example.teasystem.entity.UserFootprint;
import org.example.teasystem.mapper.FavoriteMapper;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏和足迹服务实现
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    public PageResult<Product> getFavorites(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserFavorite> favorites = favoriteMapper.selectFavorites(userId, offset, pageSize);
        long total = favoriteMapper.countFavorites(userId);

        // 查询商品详情
        List<Product> products = favorites.stream()
                .map(f -> productMapper.selectById(f.getProductId()))
                .filter(p -> p != null)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, products);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteMapper.selectFavorite(userId, productId) != null;
    }

    @Override
    public void addFavorite(Long userId, Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        // 检查是否已收藏
        if (favoriteMapper.selectFavorite(userId, productId) != null) {
            return; // 已收藏，幂等处理
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insertFavorite(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        favoriteMapper.deleteFavorite(userId, productId);
    }

    @Override
    public PageResult<Product> getFootprints(Long userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserFootprint> footprints = favoriteMapper.selectFootprints(userId, offset, pageSize);
        long total = favoriteMapper.countFootprints(userId);

        // 查询商品详情
        List<Product> products = footprints.stream()
                .map(f -> productMapper.selectById(f.getProductId()))
                .filter(p -> p != null)
                .collect(Collectors.toList());

        return PageResult.of(page, pageSize, total, products);
    }

    @Override
    public void addFootprint(Long userId, Long productId) {
        favoriteMapper.upsertFootprint(userId, productId);
    }

    @Override
    public void removeFootprint(Long userId, Long productId) {
        favoriteMapper.deleteFootprint(userId, productId);
    }

    @Override
    public void clearFootprints(Long userId) {
        favoriteMapper.clearFootprints(userId);
    }
}
