package com.genie.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDraftStatisticsVO {
    
    private Integer pendingCount;
    
    private Integer approvedCount;
    
    private Integer weeklyUpdateCount;
    
    private Integer lastWeekUpdateCount;
}
