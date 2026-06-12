package com.genie.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QaConversationVO {
    private Long id;
    private String title;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
