package com.genie.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApproveDTO {
    
    // 修改后的问题（可选，不传则使用原问题）
    @Size(max = 200, message = "问题不能超过200字符")
    private String editedQuestion;
    
    // 修改后的答案（可选，不传则使用原答案）
    @Size(max = 5000, message = "答案不能超过5000字符")
    private String editedAnswer;
}