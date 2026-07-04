package com.genie.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 审核状态：0-待审核，1-已通过
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdTime;
    
    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime reviewedTime;
    
    /**
     * 审核人
     */
    private String reviewedBy;

}