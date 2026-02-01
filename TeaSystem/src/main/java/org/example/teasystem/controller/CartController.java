package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.cart.CartItemRequest;
import org.example.teasystem.dto.cart.CartVO;
import org.example.teasystem.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 购物车控制器
 */
@Tag(name = "购物车接口")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @Operation(summary = "获取购物车")
    @GetMapping
    public Result<CartVO> getCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        CartVO cart = cartService.getCart(userId);
        return Result.success(cart);
    }
    
    @Operation(summary = "添加商品到购物车")
    @PostMapping("/items")
    public Result<Void> addItem(Authentication authentication, @Valid @RequestBody CartItemRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.addItem(userId, request);
        return Result.success();
    }
    
    @Operation(summary = "更新购物车项数量")
    @PutMapping("/items/{itemId}")
    public Result<Void> updateItemQuantity(Authentication authentication,
                                           @PathVariable Long itemId,
                                           @RequestParam Integer quantity) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.updateItemQuantity(userId, itemId, quantity);
        return Result.success();
    }
    
    @Operation(summary = "删除购物车项")
    @DeleteMapping("/items/{itemId}")
    public Result<Void> deleteItem(Authentication authentication, @PathVariable Long itemId) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.deleteItem(userId, itemId);
        return Result.success();
    }
    
    @Operation(summary = "清空购物车")
    @PostMapping("/clear")
    public Result<Void> clearCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.clearCart(userId);
        return Result.success();
    }
}
