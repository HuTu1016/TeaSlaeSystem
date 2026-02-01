package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.dto.cart.CartItemRequest;
import org.example.teasystem.dto.cart.CartVO;
import org.example.teasystem.entity.Cart;
import org.example.teasystem.entity.CartItem;
import org.example.teasystem.entity.Product;
import org.example.teasystem.entity.ProductSku;
import org.example.teasystem.mapper.CartMapper;
import org.example.teasystem.mapper.ProductMapper;
import org.example.teasystem.mapper.ProductSkuMapper;
import org.example.teasystem.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public CartVO getCart(Long userId) {
        // 获取或创建购物车
        Cart cart = getOrCreateCart(userId);

        // 查询购物车项
        List<CartItem> items = cartMapper.selectCartItems(cart.getId());

        // 构建VO
        CartVO cartVO = new CartVO();

        List<CartVO.CartItemVO> itemVOList = new ArrayList<>();
        BigDecimal checkedAmount = BigDecimal.ZERO;
        int checkedCount = 0;

        for (CartItem item : items) {
            CartVO.CartItemVO itemVO = convertToItemVO(item);
            itemVOList.add(itemVO);

            if (item.getChecked() != null && item.getChecked() == 1) {
                checkedAmount = checkedAmount.add(itemVO.getPrice().multiply(new BigDecimal(itemVO.getQuantity())));
                checkedCount += item.getQuantity();
            }
        }

        cartVO.setItems(itemVOList);
        cartVO.setCheckedAmount(checkedAmount);
        cartVO.setCheckedCount(checkedCount);

        return cartVO;
    }

    @Override
    @Transactional
    public void addItem(Long userId, CartItemRequest request) {
        // 获取或创建购物车
        Cart cart = getOrCreateCart(userId);

        // 验证SKU
        ProductSku sku = productSkuMapper.selectById(request.getSkuId());
        if (sku == null) {
            throw new BusinessException(400, "商品规格不存在");
        }

        // 验证商品
        Product product = productMapper.selectById(sku.getProductId());
        if (product == null || !"ON_SHELF".equals(product.getStatus())) {
            throw new BusinessException(400, "商品不存在或已下架");
        }

        // 检查库存
        if (sku.getStock() < request.getQuantity()) {
            throw new BusinessException(400, "库存不足");
        }

        // 检查是否已存在该商品
        CartItem existItem = cartMapper.selectCartItem(cart.getId(), request.getSkuId());

        if (existItem != null) {
            // 更新数量
            int newQuantity = existItem.getQuantity() + request.getQuantity();
            if (newQuantity > sku.getStock()) {
                throw new BusinessException(400, "超出库存数量");
            }
            cartMapper.updateItemQuantity(existItem.getId(), newQuantity);
        } else {
            // 新增购物车项
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setSkuId(request.getSkuId());
            newItem.setQuantity(request.getQuantity());
            newItem.setChecked(1);
            cartMapper.insertCartItem(newItem);
        }
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        // 获取购物车
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            throw new BusinessException(400, "购物车不存在");
        }

        // 验证购物车项归属
        CartItem item = cartMapper.selectCartItemById(itemId);
        if (item == null || !item.getCartId().equals(cart.getId())) {
            throw new BusinessException(400, "购物车项不存在");
        }

        // 验证库存
        ProductSku sku = productSkuMapper.selectById(item.getSkuId());
        if (quantity > sku.getStock()) {
            throw new BusinessException(400, "超出库存数量");
        }

        cartMapper.updateItemQuantity(itemId, quantity);
    }

    @Override
    @Transactional
    public void updateItemChecked(Long userId, Long itemId, Integer checked) {
        // 获取购物车
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            throw new BusinessException(400, "购物车不存在");
        }

        // 验证购物车项归属
        CartItem item = cartMapper.selectCartItemById(itemId);
        if (item == null || !item.getCartId().equals(cart.getId())) {
            throw new BusinessException(400, "购物车项不存在");
        }

        cartMapper.updateItemSelected(itemId, checked);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        // 获取购物车
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            throw new BusinessException(400, "购物车不存在");
        }

        // 验证购物车项归属
        CartItem item = cartMapper.selectCartItemById(itemId);
        if (item == null || !item.getCartId().equals(cart.getId())) {
            throw new BusinessException(400, "购物车项不存在");
        }

        cartMapper.deleteCartItem(itemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart != null) {
            cartMapper.deleteAllCartItems(cart.getId());
        }
    }

    /**
     * 获取或创建购物车
     */
    private Cart getOrCreateCart(Long userId) {
        Cart cart = cartMapper.selectByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }
        return cart;
    }

    /**
     * 转换为购物车项VO
     * 由于数据库 cart_item 表没有存储 product_id 和 price，需要通过 SKU 查询获取
     */
    private CartVO.CartItemVO convertToItemVO(CartItem item) {
        CartVO.CartItemVO itemVO = new CartVO.CartItemVO();
        itemVO.setId(item.getId());
        itemVO.setSkuId(item.getSkuId());
        itemVO.setQuantity(item.getQuantity());
        itemVO.setChecked(item.getChecked());

        // 先获取SKU信息，从SKU获取 product_id 和 price
        ProductSku sku = productSkuMapper.selectById(item.getSkuId());
        if (sku != null) {
            itemVO.setSkuName(sku.getSkuName());
            itemVO.setStock(sku.getStock());
            itemVO.setPrice(sku.getPrice());
            itemVO.setProductId(sku.getProductId());

            // 通过SKU的productId获取商品信息
            Product product = productMapper.selectById(sku.getProductId());
            if (product != null) {
                itemVO.setProductTitle(product.getTitle());
                itemVO.setSubtitle(product.getSubtitle());
                itemVO.setImage(product.getMainImage());
                itemVO.setValid("ON_SHELF".equals(product.getStatus()));
            } else {
                itemVO.setValid(false);
            }
        } else {
            itemVO.setValid(false);
        }

        return itemVO;
    }
}
