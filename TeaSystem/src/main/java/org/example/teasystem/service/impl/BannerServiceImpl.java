package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.entity.Banner;
import org.example.teasystem.mapper.BannerMapper;
import org.example.teasystem.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner服务实现
 */
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<Banner> getEnabledBanners() {
        return bannerMapper.selectEnabled();
    }

    @Override
    public PageResult<Banner> getBannerList(Integer status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Banner> list = bannerMapper.selectList(status, offset, pageSize);
        long total = bannerMapper.count(status);
        return PageResult.of(page, pageSize, total, list);
    }

    @Override
    public Long createBanner(Banner banner) {
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerMapper.insert(banner);
        return banner.getId();
    }

    @Override
    public void updateBanner(Banner banner) {
        Banner existing = bannerMapper.selectById(banner.getId());
        if (existing == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        bannerMapper.update(banner);
    }

    @Override
    public void enableBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        bannerMapper.updateStatus(id, 1);
    }

    @Override
    public void disableBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        bannerMapper.updateStatus(id, 0);
    }

    @Override
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
    }
}
