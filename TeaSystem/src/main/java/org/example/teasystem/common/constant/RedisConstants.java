package org.example.teasystem.common.constant;

/**
 * Redis Key 常量
 */
public class RedisConstants {

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "tea:token:";

    /**
     * 登录失败次数前缀
     */
    public static final String LOGIN_FAIL_PREFIX = "tea:login:fail:";

    /**
     * 账号锁定前缀
     */
    public static final String ACCOUNT_LOCK_PREFIX = "tea:account:lock:";

    /**
     * 热门商品缓存
     */
    public static final String HOT_PRODUCTS = "tea:products:hot";

    /**
     * 新品商品缓存
     */
    public static final String NEW_PRODUCTS = "tea:products:new";

    /**
     * 首页推荐缓存
     */
    public static final String HOME_RECOMMEND = "tea:recommend:home";

    /**
     * 搜索热词缓存
     */
    public static final String SEARCH_HOT_WORDS = "tea:search:hot";

    /**
     * 幂等键前缀
     */
    public static final String IDEMPOTENT_PREFIX = "tea:idempotent:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = "tea:captcha:";

    /**
     * 登录失败锁定时间（分钟）
     */
    public static final int LOGIN_LOCK_MINUTES = 10;

    /**
     * 最大登录失败次数
     */
    public static final int MAX_LOGIN_FAIL_COUNT = 5;

    /**
     * 热门商品缓存时间（秒）
     */
    public static final int HOT_PRODUCTS_EXPIRE = 300;

    /**
     * 推荐缓存时间（秒）
     */
    public static final int RECOMMEND_EXPIRE = 60;

    /**
     * 热词缓存时间（秒）
     */
    public static final int HOT_WORDS_EXPIRE = 60;

    /**
     * 商品列表缓存
     */
    public static final String PRODUCT_LIST = "tea:products:list:";

    /**
     * 分类树缓存
     */
    public static final String CATEGORY_TREE = "tea:categories:tree";

    /**
     * 24小时缓存时间（秒）
     */
    public static final int CACHE_24H_SECONDS = 86400;

    /**
     * 1小时缓存时间（秒）
     */
    public static final int CACHE_1H_SECONDS = 3600;
}
