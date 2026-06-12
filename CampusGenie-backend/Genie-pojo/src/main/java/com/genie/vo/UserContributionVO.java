package com.genie.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserContributionVO {
    private Long id;
    private String question;
    private String answer;
    private String categoryName;
    private String statusDesc;
    // 待审核 / 已通过 / 已驳回
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdTime;
    private LocalDateTime reviewedTime;
    private String rejectReason;
}