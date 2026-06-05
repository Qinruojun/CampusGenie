package com.genie.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class IntentBatchRecognizeDTO {
    @Valid
    @NotEmpty(message = "识别样本不能为空")
    @Size(max = 100, message = "单次批量识别不能超过100条")
    private List<IntentRecognizeDTO> items;
}
