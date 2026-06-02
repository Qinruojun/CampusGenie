package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class LoginDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}