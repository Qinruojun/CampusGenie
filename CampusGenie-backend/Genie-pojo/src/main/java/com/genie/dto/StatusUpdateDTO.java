
package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StatusUpdateDTO {
    @NotNull(message = "状态不能为空")
    private Integer status;
}
