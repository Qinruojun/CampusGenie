package com.genie.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class BatchReviewDTO {
    
    @NotEmpty(message = "请选择要审核的贡献")
    private List<Long> contributionIds;
    
    @NotNull(message = "审核动作不能为空")
    private Integer action;  // 1-批量通过，2-批量驳回
    
    @Size(max = 200, message = "驳回理由不能超过200字符")
    private String rejectReason;
}