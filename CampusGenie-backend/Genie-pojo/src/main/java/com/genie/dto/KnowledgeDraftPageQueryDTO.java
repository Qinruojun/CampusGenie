package com.genie.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 知识草稿分页查询 DTO
 */
@Data
public class KnowledgeDraftPageQueryDTO {
    
    /**
     * 当前页码，从1开始
     */
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
    
    /**
     * 审核状态：0-待审核，1-已通过
     */
    private Integer status;
    
    /**
     * 分类ID筛选
     */
    private Integer categoryId;
    
    /**
     * 关键词模糊搜索（匹配问题或答案）
     */
    private String keyword;

    // 开始时间（格式：yyyy-MM-dd HH:mm:ss）
    private String startTime;

    // 结束时间（格式：yyyy-MM-dd HH:mm:ss）
    private String endTime;

    // 排序顺序：desc-降序（按提交时间），asc-升序
    private String sortOrder = "desc";
}