package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnswerVO {//后端传给前端的回答,如果没查到答案就返回空
    private String question;
    private String answer;
    private String categoryName;
    private String source;
    private LocalDateTime updatedTime;
    private Long knowledgeId;
    private List<String> relatedQuestions;

    public List<String> getRelatedQuestions() {
        return relatedQuestions;
    }

    public void setRelatedQuestions(List<String> relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }
}
