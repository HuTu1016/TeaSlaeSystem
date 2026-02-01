package org.example.teasystem.service;

import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Banner;

import java.util.List;

/**
 * Banner服务接口
 */
public interface BannerService {

    /**
     * 获取启用的Banner列表（用户端）
     */
    List<Banner> getEnabledBanners();

    /**
     * 获取Banner列表（管理端）
     */
    PageResult<Banner> getBannerList(Integer status, Integer page, Integer pageSize);

    /**
     * 创建Banner
     */
    Long createBanner(Banner banner);

    /**
     * 更新Banner
     */
    void updateBanner(Banner banner);

    /**
     * 启用Banner
     */
    void enableBanner(Long id);

    /**
     * 禁用Banner
     */
    void disableBanner(Long id);

    /**
     * 删除Banner
     */
    void deleteBanner(Long id);
}
