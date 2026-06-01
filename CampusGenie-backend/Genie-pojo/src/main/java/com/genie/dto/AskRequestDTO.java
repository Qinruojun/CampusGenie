package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class AskRequestDTO {//前端传给后端的问题
    @NotBlank
    @Size(max = 200)
    private String question;
}