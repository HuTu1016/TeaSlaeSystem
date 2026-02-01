package org.example.teasystem.dto.aftersale;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户填写退货物流信息请求
 */
@Data
public class AfterSaleShipRequest {

    @NotBlank(message = "物流公司不能为空")
    @Size(max = 50, message = "物流公司名称最长50字符")
    private String expressCompany;

    @NotBlank(message = "物流单号不能为空")
    @Size(max = 50, message = "物流单号最长50字符")
    private String expressNo;
}
