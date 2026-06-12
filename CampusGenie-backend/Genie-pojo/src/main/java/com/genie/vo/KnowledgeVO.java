package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KnowledgeVO {
    private Long id;
    private String question;
    private String answer;
    private String categoryName;
    private String source;
    private Integer status;
    private LocalDateTime updatedTime;
}