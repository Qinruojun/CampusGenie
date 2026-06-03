package com.genie.vo;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class LoginVO {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private Integer role;

    private String token;//主要是要将这个token返回给前端，后续做request时再传递给后端获得用户
}