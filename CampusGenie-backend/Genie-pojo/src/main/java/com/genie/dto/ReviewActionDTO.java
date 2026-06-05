package com.genie.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ReviewActionDTO {//管理员审核时传给后端的信息
    @NotNull
    private Long contributionId;
    @NotNull
    private Integer action;      // 1-通过, 2-驳回
    @Size(max=200)
    private String rejectReason; // 驳回时必填
    private String editedAnswer; // 通过时可修改答案
}