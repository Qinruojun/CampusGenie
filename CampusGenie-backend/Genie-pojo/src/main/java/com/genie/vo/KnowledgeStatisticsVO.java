package com.genie.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeStatisticsVO {
    
    /**
     * 已发布数量
     */
    private Integer publishedCount;
    
    /**
     * 已停用数量
     */
    private Integer stoppedCount;
    
    /**
     * 本周更新数量
     */
    private Integer weeklyUpdateCount;
    
    /**
     * 上周更新数量（用于计算趋势）
     */
    private Integer lastWeekUpdateCount;
}
