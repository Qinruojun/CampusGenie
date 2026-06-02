package com.genie.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.io.Serializable;

/**
 * 管理员分页查询用户贡献 DTO
 */
@Data
public class AdminContributionPageQueryDTO implements Serializable {
    
    // 页码（从1开始）
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;
    
    // 每页记录数（默认10，最大100）
    private Integer pageSize = 10;
    
    // 审核状态：0-待审核，1-已通过，2-已驳回（不传则查全部）
    private Integer status;
    
    // 分类ID筛选
    private Integer categoryId;
    
    // 关键词模糊搜索（匹配问题或答案）
    private String keyword;
    
    // 提交者用户名
    private String username;
    
    // 开始时间（格式：yyyy-MM-dd HH:mm:ss）
    private String startTime;
    
    // 结束时间（格式：yyyy-MM-dd HH:mm:ss）
    private String endTime;
    
    // 排序顺序：desc-降序（按提交时间），asc-升序
    private String sortOrder = "desc";
}