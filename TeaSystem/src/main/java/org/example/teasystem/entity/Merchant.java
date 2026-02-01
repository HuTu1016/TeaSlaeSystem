package org.example.teasystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家实体
 */
@Data
@Schema(description = "商家信息")
public class Merchant implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "商家ID")
    private Long id;
    
    @Schema(description = "关联用户ID")
    private Long userId;
    
    @Schema(description = "店铺名称")
    private String shopName;
    
    @Schema(description = "店铺描述")
    private String shopDesc;
    
    @Schema(description = "店铺Logo")
    private String shopLogo;
    
    @Schema(description = "联系电话")
    private String contactPhone;
    
    @Schema(description = "营业执照URL")
    private String businessLicense;
    
    @Schema(description = "状态：1正常 0禁用")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
