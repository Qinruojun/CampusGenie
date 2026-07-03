package com.genie.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员查看用户贡献 VO
 */
@Data
public class AdminContributionVO {
    
    // 贡献ID
    private Long id;
    
    // 提交用户名
    private String username;
    
    // 问题
    private String question;
    
    // 答案
    private String answer;
    
    // 分类名称
    private String categoryName;
    
    // 补充说明
    private String supplement;
    
    // 联系方式
    private String contact;

    // 审核状态描述
    private String statusDesc;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    // 提交时间
    private LocalDateTime createdTime;
    
    // 审核时间
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai
    private LocalDateTime reviewedTime;
    
    // 驳回理由
    private String rejectReason;
}