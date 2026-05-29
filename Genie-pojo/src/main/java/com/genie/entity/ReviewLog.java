package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLog {
    private Long id;
    private Integer contributionType;   // 1-用户贡献, 2-知识草稿
    private Long contributionId;
    private String reviewer;
    private Integer action;             // 1-通过, 2-驳回
    private String originalQuestion;
    private String finalQuestion;
    private String originalAnswer;
    private String finalAnswer;
    private String rejectReason;
    private LocalDateTime createdTime;
}