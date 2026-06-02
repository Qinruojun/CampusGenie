package com.genie.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 导入错误明细 VO
 * 返回给前端，展示每行失败的原因
 */
@Data
@Builder
public class ImportErrorVO {
    
    /**
     * 行号（Excel 中从第2行开始，JSON 中从第1条开始）
     * 便于管理员定位错误数据
     */
    private Integer rowNo;
    
    /**
     * 问题内容
     */
    private String question;
    
    /**
     * 失败原因
     * 如："问题不能为空"、"问题已存在"
     */
    private String reason;
}