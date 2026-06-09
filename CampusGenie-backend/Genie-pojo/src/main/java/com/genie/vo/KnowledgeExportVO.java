package com.genie.vo;

import lombok.Data;

@Data
public class KnowledgeExportVO {
    private String question;      // 问题
    private String answer;        // 答案
    private String categoryName;  // 分类
    private String source;        // 来源
}