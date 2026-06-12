package com.genie.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KnowledgeVO {
    private Long id;
    private String question;
    private String answer;
    private String category;
    private String source;
    private Integer status;
    private String updatedBy;
    
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;
}