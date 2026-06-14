package com.genie.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识草稿 VO
 */
@Data
public class KnowledgeDraftVO {
    
    /**
     * 草稿ID
     */
    private Long id;
    
    /**
     * 问题
     */
    private String question;
    
    /**
     * 答案
     */
    private String answer;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 信息来源（如“搜索引擎补充”、“爬虫-官网”）
     */
    private String source;
    
    /**
     * 审核状态描述：待审核 / 已通过 / 已驳回
     */
    private String statusDesc;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewedTime;

}