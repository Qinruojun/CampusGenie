package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
//Entity是数据库表在java里的映射，数据库表和entity类一一对应，是java中的实体类
@Data   //@Data注释会自动加getter和setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;//AUTO_INCREMENT不用插入
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