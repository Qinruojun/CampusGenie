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
public class KnowledgeDraft {
    private Long id;
    private String question;
    private String answer;
    private Integer categoryId;
    private String source;
    private Integer status;          // 0-待审核, 1-通过
    private String reviewedBy;
    private LocalDateTime reviewedTime;
    private LocalDateTime createdTime;
}