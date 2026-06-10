package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data   //会自动加getter和setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}