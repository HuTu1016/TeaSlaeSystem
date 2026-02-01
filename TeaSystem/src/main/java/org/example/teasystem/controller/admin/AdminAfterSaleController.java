package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.PageResult;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.entity.AfterSale;
import org.example.teasystem.entity.User;
import org.example.teasystem.entity.Merchant;
import org.example.teasystem.mapper.AfterSaleMapper;
import org.example.teasystem.mapper.UserMapper;
import org.example.teasystem.mapper.MerchantMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端售后管理控制器
 */
@Tag(name = "管理端-售后管理")
@RestController
@RequestMapping("/admin/after-sales")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAfterSaleController {

    private final AfterSaleMapper afterSaleMapper;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;

    @Operation(summary = "获取售后列表")
    @GetMapping
    public Result<PageResult<AfterSaleVO>> getAfterSaleList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String afterSaleNo,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        int offset = (page - 1) * pageSize;
        List<AfterSale> list = afterSaleMapper.selectByAdmin(status, type, merchantId, userId, 
                afterSaleNo, offset, pageSize);
        long total = afterSaleMapper.countByAdmin(status, type, merchantId, userId, afterSaleNo);
        
        List<AfterSaleVO> voList = list.stream().map(this::convertToVO).collect(Collectors.toList());
        
        PageResult<AfterSaleVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取售后详情")
    @GetMapping("/{afterSaleId}")
    public Result<AfterSaleVO> getAfterSaleDetail(@PathVariable Long afterSaleId) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            return Result.fail("售后记录不存在");
        }
        return Result.success(convertToVO(afterSale));
    }

    @Operation(summary = "管理员介入售后")
    @PostMapping("/{afterSaleId}/intervene")
    public Result<Void> intervene(@PathVariable Long afterSaleId, @RequestBody InterveneRequest request) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            return Result.fail("售后记录不存在");
        }
        afterSaleMapper.adminIntervene(afterSaleId, request.getStatus(), request.getComment());
        return Result.success();
    }

    @Operation(summary = "强制完成售后")
    @PostMapping("/{afterSaleId}/complete")
    public Result<Void> forceComplete(@PathVariable Long afterSaleId, @RequestParam(required = false) String comment) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            return Result.fail("售后记录不存在");
        }
        afterSaleMapper.adminIntervene(afterSaleId, "COMPLETED", comment);
        afterSaleMapper.updateCompleted(afterSaleId, LocalDateTime.now());
        return Result.success();
    }

    @Operation(summary = "强制取消售后")
    @PostMapping("/{afterSaleId}/cancel")
    public Result<Void> forceCancel(@PathVariable Long afterSaleId, @RequestParam(required = false) String reason) {
        AfterSale afterSale = afterSaleMapper.selectById(afterSaleId);
        if (afterSale == null) {
            return Result.fail("售后记录不存在");
        }
        afterSaleMapper.adminIntervene(afterSaleId, "CANCELLED", reason);
        return Result.success();
    }

    @Operation(summary = "获取售后统计")
    @GetMapping("/stats")
    public Result<AfterSaleStatsVO> getAfterSaleStats() {
        AfterSaleStatsVO stats = new AfterSaleStatsVO();
        stats.setTotalAfterSales(afterSaleMapper.countAll());
        stats.setTodayAfterSales(afterSaleMapper.countToday());
        stats.setPendingAfterSales(afterSaleMapper.countByAdmin("PENDING", null, null, null, null));
        stats.setProcessingAfterSales(afterSaleMapper.countByAdmin("PROCESSING", null, null, null, null));
        stats.setCompletedAfterSales(afterSaleMapper.countByAdmin("COMPLETED", null, null, null, null));
        return Result.success(stats);
    }

    /**
     * 转换为VO
     */
    private AfterSaleVO convertToVO(AfterSale afterSale) {
        AfterSaleVO vo = new AfterSaleVO();
        BeanUtils.copyProperties(afterSale, vo);
        
        // 获取用户信息
        if (afterSale.getUserId() != null) {
            User user = userMapper.findById(afterSale.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setUserPhone(user.getPhone());
            }
        }
        
        // 获取商家信息
        if (afterSale.getMerchantId() != null) {
            Merchant merchant = merchantMapper.findById(afterSale.getMerchantId());
            if (merchant != null) {
                vo.setMerchantName(merchant.getShopName());
            }
        }
        
        return vo;
    }

    /**
     * 售后VO
     */
    @lombok.Data
    public static class AfterSaleVO {
        private Long id;
        private String afterSaleNo;
        private Long orderId;
        private Long orderItemId;
        private Long userId;
        private String userName;
        private String userPhone;
        private Long merchantId;
        private String merchantName;
        private String type;
        private String status;
        private String reason;
        private String description;
        private String images;
        private BigDecimal applyAmount;
        private String merchantComment;
        private String adminComment;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }

    /**
     * 介入请求
     */
    @lombok.Data
    public static class InterveneRequest {
        private String status;
        private String comment;
    }

    /**
     * 售后统计VO
     */
    @lombok.Data
    public static class AfterSaleStatsVO {
        private Long totalAfterSales;
        private Long todayAfterSales;
        private Long pendingAfterSales;
        private Long processingAfterSales;
        private Long completedAfterSales;
    }
}
