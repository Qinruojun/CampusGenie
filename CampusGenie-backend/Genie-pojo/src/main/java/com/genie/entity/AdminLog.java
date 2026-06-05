package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLog {
    private Long id;
    private String adminName;
    private String actionType;
    private String targetType;
    private Long targetId;
    private String detail;
    private LocalDateTime createdTime;
}
