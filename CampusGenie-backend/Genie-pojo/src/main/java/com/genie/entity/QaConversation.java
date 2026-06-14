package com.genie.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QaConversation {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer deleted;
}
