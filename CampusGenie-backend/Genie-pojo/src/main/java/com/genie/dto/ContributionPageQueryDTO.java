package com.genie.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.io.Serializable;

/**
 * 用户贡献分页查询DTO
 */
@Data
public class ContributionPageQueryDTO implements Serializable {
    
    // 页码（从1开始）
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    // 每页记录数
    private Integer pageSize = 10;
    
    // 分类ID
    private Integer categoryId;
    
    // 审核状态：0-待审核，1-已通过，2-已驳回
    private Integer status;
    
    // 关键词（模糊匹配问题或答案）
    private String keyword;
    
    // 排序顺序：desc-降序（按时间），asc-升序
    private String sortOrder = "desc";
}