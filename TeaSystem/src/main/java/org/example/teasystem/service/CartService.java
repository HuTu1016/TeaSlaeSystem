package org.example.teasystem.service;

import org.example.teasystem.dto.cart.CartItemRequest;
import org.example.teasystem.dto.cart.CartVO;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 获取购物车
     */
    CartVO getCart(Long userId);
    
    /**
     * 添加商品到购物车
     */
    void addItem(Long userId, CartItemRequest request);
    
    /**
     * 更新购物车项数量
     */
    void updateItemQuantity(Long userId, Long itemId, Integer quantity);
    
    /**
     * 更新购物车项选中状态
     */
    void updateItemChecked(Long userId, Long itemId, Integer checked);
    
    /**
     * 删除购物车项
     */
    void deleteItem(Long userId, Long itemId);
    
    /**
     * 清空购物车
     */
    void clearCart(Long userId);
}
