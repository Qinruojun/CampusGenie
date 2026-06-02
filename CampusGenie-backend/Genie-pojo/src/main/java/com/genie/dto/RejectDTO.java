package com.genie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectDTO {
    
    @NotBlank(message = "驳回理由不能为空")
    @Size(max = 200, message = "驳回理由不能超过200字符")
    private String rejectReason;
}