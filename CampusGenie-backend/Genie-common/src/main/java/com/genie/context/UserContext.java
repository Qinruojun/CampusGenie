package com.genie.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//用户信息上下文
public class UserContext {
    private Long userId;
    private String username;
    private Integer role;
}