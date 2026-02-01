package org.example.teasystem.dto.request;

import lombok.Data;
import javax.validation.constraints.Min;

/**
 * 购物车更新请求
 */
@Data
public class CartUpdateRequest {

    /**
     * 数量
     */
    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;

    /**
     * 是否选中
     */
    private Boolean selected;
}
