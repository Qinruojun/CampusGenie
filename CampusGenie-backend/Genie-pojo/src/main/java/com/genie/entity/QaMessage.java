package com.genie.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QaMessage {
    private Long id;
    private Long conversationId;
    private Long userId;
    private String role;
    private String content;
    private LocalDateTime createdTime;
}
