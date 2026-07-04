package com.genie.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private LocalDateTime createdTime;
}
