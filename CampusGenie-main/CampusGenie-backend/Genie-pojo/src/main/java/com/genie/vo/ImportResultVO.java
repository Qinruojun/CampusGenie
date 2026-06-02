package com.genie.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 导入结果 VO
 * 返回给前端，展示导入统计和失败明细
 */
@Data
@Builder
public class ImportResultVO {
    
    /**
     * 总处理行数
     */
    private Integer totalCount;
    
    /**
     * 成功导入条数
     */
    private Integer successCount;
    
    /**
     * 失败条数
     */
    private Integer failCount;
    
    /**
     * 失败明细列表
     */
    private List<ImportErrorVO> errors;
    
    /**
     * 汇总信息（前端可直接展示）
     * 示例："成功导入 7 条，失败 3 条"
     */
    private String summary;
    
    /**
     * 导入完成时间
     */
    private LocalDateTime importTime;
}