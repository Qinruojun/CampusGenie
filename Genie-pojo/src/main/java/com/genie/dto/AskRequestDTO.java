package com.genie.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AskRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String question;
}