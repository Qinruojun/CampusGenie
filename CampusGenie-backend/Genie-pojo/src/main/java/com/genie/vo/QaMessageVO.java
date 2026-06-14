package com.genie.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QaMessageVO {
    private Long id;
    private Long conversationId;
    private String role;
    private String content;
    private LocalDateTime createdTime;
}
