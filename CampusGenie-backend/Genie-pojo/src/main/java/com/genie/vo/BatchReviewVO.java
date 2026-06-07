package com.genie.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchReviewVO {
    
    /**
     * 成功审核数量
     */
    private Integer successCount;
    
    /**
     * 失败数量
     */
    private Integer failCount;
    
    /**
     * 失败的贡献ID列表
     */
    private List<Long> failIds;
    
    /**
     * 失败原因映射
     */
    private Map<Long, String> failReasons;
}