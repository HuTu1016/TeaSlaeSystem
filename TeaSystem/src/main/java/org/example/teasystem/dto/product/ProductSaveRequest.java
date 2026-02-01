package org.example.teasystem.dto.product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品保存请求
 */
@Data
public class ProductSaveRequest {
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 120, message = "商品标题最长120字符")
    private String title;
    
    @Size(max = 200, message = "副标题最长200字符")
    private String subtitle;
    
    @Size(max = 50, message = "产地最长50字符")
    private String origin;
    
    @Size(max = 50, message = "工艺最长50字符")
    private String process;
    
    @Size(max = 200, message = "口感描述最长200字符")
    private String taste;
    
    private String brewMethod;
    
    private String environment;
    
    private String mainImage;
    
    /**
     * 商品图片列表
     */
    private List<String> images;
    
    /**
     * SKU列表
     */
    private List<SkuSaveRequest> skus;
    
    @Data
    public static class SkuSaveRequest {
        private Long id;
        
        @NotBlank(message = "SKU名称不能为空")
        @Size(max = 80, message = "SKU名称最长80字符")
        private String skuName;
        
        @NotNull(message = "价格不能为空")
        private BigDecimal price;
        
        @NotNull(message = "库存不能为空")
        private Integer stock;
        
        private Integer status = 1;
    }
}
