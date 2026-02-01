package org.example.teasystem.mapper;

import org.apache.ibatis.annotations.*;
import org.example.teasystem.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper
 */
@Mapper
public interface ProductMapper {

        /**
         * 根据ID查询商品
         */
        @Select("SELECT * FROM product WHERE id = #{id}")
        Product findById(@Param("id") Long id);

        /**
         * 分页查询商品列表
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE 1=1 " +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='origin != null and origin != \"\"'>AND origin = #{origin}</if>" +
                        "<if test='process != null and process != \"\"'>AND process = #{process}</if>" +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<choose>" +
                        "  <when test='sort == \"sales_desc\"'>ORDER BY sales_count DESC</when>" +
                        "  <when test='sort == \"price_asc\"'>ORDER BY min_price ASC</when>" +
                        "  <when test='sort == \"price_desc\"'>ORDER BY min_price DESC</when>" +
                        "  <when test='sort == \"newest\"'>ORDER BY created_at DESC</when>" +
                        "  <otherwise>ORDER BY id DESC</otherwise>" +
                        "</choose>" +
                        " LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Product> findPage(@Param("merchantId") Long merchantId, @Param("categoryId") Long categoryId,
                        @Param("status") String status, @Param("origin") String origin,
                        @Param("process") String process, @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice, @Param("keyword") String keyword,
                        @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计商品数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE 1=1 " +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "<if test='origin != null and origin != \"\"'>AND origin = #{origin}</if>" +
                        "<if test='process != null and process != \"\"'>AND process = #{process}</if>" +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "</script>")
        long count(@Param("merchantId") Long merchantId, @Param("categoryId") Long categoryId,
                        @Param("status") String status, @Param("origin") String origin,
                        @Param("process") String process, @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice, @Param("keyword") String keyword);

        /**
         * 插入商品
         */
        @Insert("INSERT INTO product (merchant_id, category_id, title, subtitle, origin, process, taste, " +
                        "brew_method, environment, main_image, status, min_price, max_price, sales_count, view_count, created_at) "
                        +
                        "VALUES (#{merchantId}, #{categoryId}, #{title}, #{subtitle}, #{origin}, #{process}, #{taste}, "
                        +
                        "#{brewMethod}, #{environment}, #{mainImage}, #{status}, #{minPrice}, #{maxPrice}, 0, 0, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Product product);

        /**
         * 更新商品
         */
        @Update("UPDATE product SET category_id = #{categoryId}, title = #{title}, subtitle = #{subtitle}, " +
                        "origin = #{origin}, process = #{process}, taste = #{taste}, brew_method = #{brewMethod}, " +
                        "environment = #{environment}, main_image = #{mainImage}, updated_at = NOW() WHERE id = #{id}")
        int update(Product product);

        /**
         * 更新商品状态
         */
        @Update("UPDATE product SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status);

        /**
         * 更新商品价格区间
         */
        @Update("UPDATE product SET min_price = #{minPrice}, max_price = #{maxPrice}, updated_at = NOW() WHERE id = #{id}")
        int updatePriceRange(@Param("id") Long id, @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice);

        /**
         * 增加销量
         */
        @Update("UPDATE product SET sales_count = sales_count + #{count}, updated_at = NOW() WHERE id = #{id}")
        int increaseSalesCount(@Param("id") Long id, @Param("count") int count);

        /**
         * 增加浏览量
         */
        @Update("UPDATE product SET view_count = view_count + 1 WHERE id = #{id}")
        int increaseViewCount(@Param("id") Long id);

        /**
         * 查询热门商品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' ORDER BY sales_count DESC LIMIT #{limit}")
        List<Product> findHotProducts(@Param("limit") int limit);

        /**
         * 查询新品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' ORDER BY created_at DESC LIMIT #{limit}")
        List<Product> findNewProducts(@Param("limit") int limit);

        /**
         * 查询相似商品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' AND category_id = #{categoryId} AND id != #{excludeId} "
                        +
                        "ORDER BY ABS(min_price - #{price}) ASC LIMIT #{limit}")
        List<Product> findSimilarProducts(@Param("categoryId") Long categoryId, @Param("price") BigDecimal price,
                        @Param("excludeId") Long excludeId, @Param("limit") int limit);

        // ==================== 新增方法 ====================

        /**
         * 根据ID查询商品（别名）
         */
        @Select("SELECT * FROM product WHERE id = #{id}")
        Product selectById(@Param("id") Long id);

        /**
         * 分页查询商品列表
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE status = 1 " +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (name LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='minPrice != null'>AND price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND price &lt;= #{maxPrice}</if>" +
                        "<choose>" +
                        "  <when test='sortBy == \"price\" and sortOrder == \"asc\"'>ORDER BY price ASC</when>" +
                        "  <when test='sortBy == \"price\" and sortOrder == \"desc\"'>ORDER BY price DESC</when>" +
                        "  <when test='sortBy == \"sales\"'>ORDER BY sales_count DESC</when>" +
                        "  <otherwise>ORDER BY created_at DESC</otherwise>" +
                        "</choose>" +
                        " LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Product> selectProductList(@Param("categoryId") Long categoryId, @Param("keyword") String keyword,
                        @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
                        @Param("sortBy") String sortBy, @Param("sortOrder") String sortOrder,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计商品数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE status = 1 " +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (name LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='minPrice != null'>AND price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND price &lt;= #{maxPrice}</if>" +
                        "</script>")
        int countProducts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword,
                        @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

        /**
         * 根据商家ID查询商品列表
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
                        "</script>")
        List<Product> selectByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status,
                        @Param("offset") int offset, @Param("size") int size);

        /**
         * 统计商家商品数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE merchant_id = #{merchantId} " +
                        "<if test='status != null'>AND status = #{status}</if>" +
                        "</script>")
        int countByMerchantId(@Param("merchantId") Long merchantId, @Param("status") String status);

        // ==================== ServiceImpl所需方法 ====================

        /**
         * 分页查询商品列表（简化版，支持多分类ID）
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE status = 'ON_SHELF' " +
                        "<if test='categoryIds != null and categoryIds.size() > 0'>AND category_id IN <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>"
                        +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        "<choose>" +
                        "  <when test='sort == \"sales_desc\"'>ORDER BY sales_count DESC</when>" +
                        "  <when test='sort == \"price_asc\"'>ORDER BY min_price ASC</when>" +
                        "  <when test='sort == \"price_desc\"'>ORDER BY min_price DESC</when>" +
                        "  <when test='sort == \"newest\"'>ORDER BY created_at DESC</when>" +
                        "  <otherwise>ORDER BY id DESC</otherwise>" +
                        "</choose>" +
                        " LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Product> selectList(@Param("categoryIds") List<Long> categoryIds, @Param("keyword") String keyword,
                        @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
                        @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计商品数量（简化版，支持多分类ID）
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE status = 'ON_SHELF' " +
                        "<if test='categoryIds != null and categoryIds.size() > 0'>AND category_id IN <foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>"
                        +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        "</script>")
        long countByQuery(@Param("categoryIds") List<Long> categoryIds, @Param("keyword") String keyword,
                        @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

        /**
         * 查询热门商品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' ORDER BY sales_count DESC LIMIT #{limit}")
        List<Product> selectHot(@Param("limit") int limit);

        /**
         * 查询新品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' ORDER BY created_at DESC LIMIT #{limit}")
        List<Product> selectNew(@Param("limit") int limit);

        /**
         * 查询相似商品（排除当前商品）
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' AND category_id = #{categoryId} AND id != #{excludeId} "
                        +
                        "ORDER BY sales_count DESC LIMIT #{limit}")
        List<Product> selectSimilar(@Param("categoryId") Long categoryId, @Param("excludeId") Long excludeId,
                        @Param("limit") int limit);

        /**
         * 查询指定类目的热门商品
         */
        @Select("SELECT * FROM product WHERE status = 'ON_SHELF' AND category_id = #{categoryId} " +
                        "ORDER BY sales_count DESC LIMIT #{limit}")
        List<Product> selectHotByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

        /**
         * 搜索商品（带排序）
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE status = 'ON_SHELF' " +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%') OR origin LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='origin != null and origin != \"\"'>AND origin = #{origin}</if>" +
                        "<if test='process != null and process != \"\"'>AND process = #{process}</if>" +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        " ORDER BY ${orderBy} LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                        @Param("origin") String origin, @Param("process") String process,
                        @Param("minPrice") java.math.BigDecimal minPrice,
                        @Param("maxPrice") java.math.BigDecimal maxPrice,
                        @Param("orderBy") String orderBy, @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计搜索商品数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE status = 'ON_SHELF' " +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%') OR subtitle LIKE CONCAT('%',#{keyword},'%') OR origin LIKE CONCAT('%',#{keyword},'%'))</if>"
                        +
                        "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
                        "<if test='origin != null and origin != \"\"'>AND origin = #{origin}</if>" +
                        "<if test='process != null and process != \"\"'>AND process = #{process}</if>" +
                        "<if test='minPrice != null'>AND min_price &gt;= #{minPrice}</if>" +
                        "<if test='maxPrice != null'>AND max_price &lt;= #{maxPrice}</if>" +
                        "</script>")
        long countSearchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                        @Param("origin") String origin, @Param("process") String process,
                        @Param("minPrice") java.math.BigDecimal minPrice,
                        @Param("maxPrice") java.math.BigDecimal maxPrice);

        /**
         * 获取标题搜索建议（模糊匹配，按销量排序）
         */
        @Select("SELECT title FROM (SELECT title, MAX(sales_count) as max_sales FROM product WHERE status = 'ON_SHELF' AND title LIKE CONCAT('%',#{keyword},'%') GROUP BY title ORDER BY max_sales DESC LIMIT #{limit}) t")
        List<String> selectTitleSuggestions(@Param("keyword") String keyword, @Param("limit") int limit);

        /**
         * 获取热销商品标题（用于热搜词备选）
         */
        @Select("SELECT title FROM product WHERE status = 'ON_SHELF' ORDER BY sales_count DESC LIMIT #{limit}")
        List<String> selectHotProductTitles(@Param("limit") int limit);

        // ==================== 商品审核（管理端） ====================

        /**
         * 统计待审核商品数量
         */
        @Select("SELECT COUNT(*) FROM product WHERE status = 'PENDING'")
        Long countPending();

        /**
         * 分页查询待审核商品
         */
        @Select("<script>" +
                        "SELECT * FROM product WHERE status = 'PENDING' " +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%'))</if>" +
                        " ORDER BY created_at ASC LIMIT #{offset}, #{pageSize}" +
                        "</script>")
        List<Product> selectPendingPage(@Param("merchantId") Long merchantId, @Param("keyword") String keyword,
                        @Param("offset") int offset, @Param("pageSize") int pageSize);

        /**
         * 统计待审核商品数量（带条件）
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM product WHERE status = 'PENDING' " +
                        "<if test='merchantId != null'>AND merchant_id = #{merchantId}</if>" +
                        "<if test='keyword != null and keyword != \"\"'>AND (title LIKE CONCAT('%',#{keyword},'%'))</if>" +
                        "</script>")
        long countPendingByCondition(@Param("merchantId") Long merchantId, @Param("keyword") String keyword);

        /**
         * 审核商品（更新状态和审核信息）
         */
        @Update("UPDATE product SET status = #{status}, audit_comment = #{auditComment}, " +
                        "audited_at = NOW(), audited_by = #{auditedBy}, updated_at = NOW() WHERE id = #{id}")
        int audit(@Param("id") Long id, @Param("status") String status,
                        @Param("auditComment") String auditComment, @Param("auditedBy") Long auditedBy);
}
