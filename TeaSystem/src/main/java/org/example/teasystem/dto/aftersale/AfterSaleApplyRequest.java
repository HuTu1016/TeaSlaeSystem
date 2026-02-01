package org.example.teasystem.dto.aftersale;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 售后申请请求
 */
@Data
public class AfterSaleApplyRequest {
    
    @NotNull(message = "订单项ID不能为空")
    private Long orderItemId;
    
    @NotBlank(message = "售后类型不能为空")
    @Pattern(regexp = "^(REFUND_ONLY|RETURN_REFUND|EXCHANGE)$", message = "售后类型不合法")
    private String type;
    
    @NotBlank(message = "申请原因不能为空")
    @Size(max = 200, message = "申请原因最长200字符")
    private String reason;
    
    @Size(max = 500, message = "详细描述最长500字符")
    private String description;
    
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于0")
    private BigDecimal applyAmount;
    
    @Size(max = 6, message = "最多上传6张图片")
    private List<String> images;
}
