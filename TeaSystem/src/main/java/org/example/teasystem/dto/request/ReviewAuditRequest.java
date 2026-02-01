package org.example.teasystem.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 评论审核请求
 */
public class ReviewAuditRequest {

    /**
     * 审核状态：1-通过 2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态无效")
    @Max(value = 2, message = "审核状态无效")
    private Integer status;

    /**
     * 拒绝原因
     */
    private String reason;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
