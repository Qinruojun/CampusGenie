package com.genie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IntentRecognizeDTO {
    @NotBlank(message = "用户问题不能为空")
    @Size(max = 200, message = "问题不能超过200字符")
    private String query;

    private String traceId;
}
