package com.genie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QaConversationRenameDTO {
    @NotBlank(message = "对话标题不能为空")
    @Size(max = 24, message = "对话标题不能超过24个字符")
    private String title;
}
