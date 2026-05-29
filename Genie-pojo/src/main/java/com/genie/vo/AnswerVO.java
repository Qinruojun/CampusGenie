package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnswerVO {
    private String question;
    private String answer;
    private String categoryName;
    private String source;
    private LocalDateTime updatedTime;
}