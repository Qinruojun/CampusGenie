package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class KnowledgeVO {//知识库管理显示Knowledge信息
    private Long id;
    private String question;
    private String answer;
    private String categoryName;
    private String source;
    private LocalDateTime updatedTime;
}