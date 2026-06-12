package com.genie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QaMessageSendDTO {
    @NotBlank
    @Size(max = 200)
    private String question;
}
