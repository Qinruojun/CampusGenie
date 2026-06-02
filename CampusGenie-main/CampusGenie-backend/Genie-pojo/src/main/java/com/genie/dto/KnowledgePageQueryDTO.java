package com.genie.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.io.Serializable;

/**
 * 知识库分页查询DTO
 */
@Data
public class KnowledgePageQueryDTO implements Serializable {
    
    // 页码
    @Min(value = 1, message = "页码不能小于1")
    private Integer page;
    
    // 每页记录数
    private Integer pageSize;
    
    // 关键词（模糊匹配问题或答案）
    private String keyword;
    
    // 分类ID
    private Integer categoryId;
    
    // 状态：1-发布，0-停用
    private Integer status;
    
    // 排序顺序：desc-降序，asc-升序
    private String sortOrder;
}