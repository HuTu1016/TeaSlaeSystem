package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Cart;
import org.example.teasystem.entity.CartItem;

import java.util.List;

/**
 * 购物车Mapper
 */
@Mapper
public interface CartMapper {

        /**
         * 根据用户ID查询购物车
         */
        @Select("SELECT * FROM cart WHERE user_id = #{userId}")
        Cart findByUserId(@Param("userId") Long userId);

        /**
         * 插入购物车
         */
        @Insert("INSERT INTO cart (user_id, created_at) VALUES (#{userId}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Cart cart);

        /**
         * 根据购物车ID查询购物车项
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} ORDER BY created_at DESC")
        List<CartItem> findItemsByCartId(@Param("cartId") Long cartId);

        /**
         * 根据购物车ID和SKU ID查询购物车项
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND sku_id = #{skuId}")
        CartItem findItemByCartIdAndSkuId(@Param("cartId") Long cartId, @Param("skuId") Long skuId);

        /**
         * 根据ID查询购物车项
         */
        @Select("SELECT * FROM cart_item WHERE id = #{id}")
        CartItem findItemById(@Param("id") Long id);

        /**
         * 插入购物车项
         */
        @Insert("INSERT INTO cart_item (cart_id, sku_id, quantity, checked, created_at) " +
                        "VALUES (#{cartId}, #{skuId}, #{quantity}, #{checked}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertItem(CartItem item);

        /**
         * 更新购物车项数量
         */
        @Update("UPDATE cart_item SET quantity = #{quantity}, updated_at = NOW() WHERE id = #{id}")
        int updateItemQuantity(@Param("id") Long id, @Param("quantity") int quantity);

        /**
         * 更新购物车项选中状态
         */
        @Update("UPDATE cart_item SET checked = #{checked}, updated_at = NOW() WHERE id = #{id}")
        int updateItemChecked(@Param("id") Long id, @Param("checked") int checked);

        /**
         * 删除购物车项
         */
        @Delete("DELETE FROM cart_item WHERE id = #{id}")
        int deleteItemById(@Param("id") Long id);

        /**
         * 清空购物车
         */
        @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId}")
        int clearItems(@Param("cartId") Long cartId);

        /**
         * 删除选中的购物车项
         */
        @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId} AND checked = 1")
        int deleteCheckedItems(@Param("cartId") Long cartId);

        /**
         * 查询选中的购物车项
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND checked = 1")
        List<CartItem> findCheckedItems(@Param("cartId") Long cartId);

        // ==================== 新增方法 ====================

        /**
         * 根据用户ID查询购物车（别名）
         */
        @Select("SELECT * FROM cart WHERE user_id = #{userId}")
        Cart selectByUserId(@Param("userId") Long userId);

        /**
         * 查询购物车项
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} ORDER BY created_at DESC")
        List<CartItem> selectCartItems(@Param("cartId") Long cartId);

        /**
         * 查询购物车项（根据SKU）
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND sku_id = #{skuId}")
        CartItem selectCartItem(@Param("cartId") Long cartId, @Param("skuId") Long skuId);

        /**
         * 根据ID查询购物车项
         */
        @Select("SELECT * FROM cart_item WHERE id = #{id}")
        CartItem selectCartItemById(@Param("id") Long id);

        /**
         * 插入购物车项
         */
        @Insert("INSERT INTO cart_item (cart_id, sku_id, quantity, checked, created_at) " +
                        "VALUES (#{cartId}, #{skuId}, #{quantity}, #{checked}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insertCartItem(CartItem item);

        /**
         * 更新选中状态
         */
        @Update("UPDATE cart_item SET selected = #{selected}, updated_at = NOW() WHERE id = #{id}")
        int updateItemSelected(@Param("id") Long id, @Param("selected") int selected);

        /**
         * 删除购物车项
         */
        @Delete("DELETE FROM cart_item WHERE id = #{id}")
        int deleteCartItem(@Param("id") Long id);

        /**
         * 清空购物车
         */
        @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId}")
        int deleteAllCartItems(@Param("cartId") Long cartId);

        /**
         * 全选/取消全选
         */
        @Update("UPDATE cart_item SET selected = #{selected}, updated_at = NOW() WHERE cart_id = #{cartId}")
        int updateAllItemsSelected(@Param("cartId") Long cartId, @Param("selected") int selected);

        /**
         * 查询选中的购物车项
         */
        @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND selected = 1")
        List<CartItem> selectSelectedCartItems(@Param("cartId") Long cartId);

        /**
         * 根据ID列表查询购物车项
         */
        @Select("<script>" +
                        "SELECT * FROM cart_item WHERE cart_id = #{cartId} AND id IN " +
                        "<foreach item='id' collection='itemIds' open='(' separator=',' close=')'>#{id}</foreach>" +
                        "</script>")
        List<CartItem> selectCartItemsByIds(@Param("cartId") Long cartId, @Param("itemIds") List<Long> itemIds);

        /**
         * 批量删除购物车项
         */
        @Delete("<script>" +
                        "DELETE FROM cart_item WHERE id IN " +
                        "<foreach item='id' collection='itemIds' open='(' separator=',' close=')'>#{id}</foreach>" +
                        "</script>")
        int deleteCartItemsByIds(@Param("itemIds") List<Long> itemIds);
}
