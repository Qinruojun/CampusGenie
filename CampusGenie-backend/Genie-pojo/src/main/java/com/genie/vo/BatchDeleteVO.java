package com.genie.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量删除操作结果 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeleteVO {
    
    /**
     * 成功删除的数量
     */
    private Integer successCount;
    
    /**
     * 失败的数量
     */
    private Integer failCount;
    
    /**
     * 失败的ID列表
     */
    private List<Long> failIds;
}