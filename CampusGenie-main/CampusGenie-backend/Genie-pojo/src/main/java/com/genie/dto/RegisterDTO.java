package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册数据传输对象
 */
@Data
public class RegisterDTO {

    @NotBlank()
    private String username;

    @NotBlank()
    private String password;

    private String email;

    private String phone;   // 可选，但若提供则校验格式
}