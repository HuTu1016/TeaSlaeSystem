package org.example.teasystem.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 售后申请请求
 */
public class AfterSaleCreateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 售后类型：1-仅退款 2-退货退款 3-换货
     */
    @NotNull(message = "售后类型不能为空")
    @Min(value = 1, message = "售后类型无效")
    private Integer type;

    /**
     * 申请原因
     */
    @NotNull(message = "申请原因不能为空")
    private String reason;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 图片凭证
     */
    private List<String> images;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
