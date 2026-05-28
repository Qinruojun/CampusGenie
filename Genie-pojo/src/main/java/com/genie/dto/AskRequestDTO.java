package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AskRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String question;
}