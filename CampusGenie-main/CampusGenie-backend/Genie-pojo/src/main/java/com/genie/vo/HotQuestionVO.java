package com.genie.vo;

import lombok.Data;

@Data
public class HotQuestionVO {
    private Integer rank;         // 排名
    private String question;      // 问题文本
    private Integer queryCount;   // 查询次数
    private String trend;         // 趋势：up / down / flat
}