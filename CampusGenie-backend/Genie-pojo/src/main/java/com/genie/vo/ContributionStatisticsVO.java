package com.genie.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionStatisticsVO {
    
    /**
     * 待审核数量
     */
    private Integer pendingCount;
    
    /**
     * 已通过数量
     */
    private Integer approvedCount;
    
    /**
     * 已驳回数量
     */
    private Integer rejectedCount;
}
