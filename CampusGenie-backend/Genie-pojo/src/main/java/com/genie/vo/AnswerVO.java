package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnswerVO {//后端传给前端的回答,如果没查到答案就返回空
    private String question;
    private String answer;
    private String categoryName;
    private String source;
    private LocalDateTime updatedTime;
    private Long knowledgeId;
}
