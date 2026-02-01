package org.example.teasystem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // 私有构造函数防止实例化
    }

    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID
     * @throws RuntimeException 如果用户未登录
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            // JwtAuthenticationFilter 直接设置 userId 为 principal
            return (Long) principal;
        } else if (principal instanceof UserDetails) {
            // 假设用户名存储的是用户ID
            return Long.parseLong(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Long.parseLong((String) principal);
        }
        throw new RuntimeException("无法获取用户ID");
    }

    /**
     * 获取当前登录用户ID（可为空）
     * 
     * @return 用户ID，未登录时返回null
     */
    public static Long getCurrentUserIdOrNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return (Long) principal;
            } else if (principal instanceof UserDetails) {
                return Long.parseLong(((UserDetails) principal).getUsername());
            } else if (principal instanceof String) {
                return Long.parseLong((String) principal);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前商家ID
     * TODO: 需要根据实际UserDetails实现调整
     * 
     * @return 商家ID
     */
    public static Long getCurrentMerchantId() {
        // 实际项目中应从UserDetails中获取merchantId
        // 这里暂时通过userId查询商家表获取merchantId
        // 需要注入MerchantMapper，建议改为Controller中通过Service获取
        Long userId = getCurrentUserId();
        // 临时方案：假设merchantId等于userId（实际项目需要查询数据库）
        return userId;
    }

    /**
     * 检查当前用户是否已认证
     * 
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 检查当前用户是否有指定角色
     * 
     * @param role 角色名称
     * @return 是否有该角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}
