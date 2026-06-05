package com.genie.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IntentResultVO {
    private String query;
    private String intent;
    private String intentCode;
    private Integer labelId;
    private BigDecimal confidence;
    private String status;
    private String message;
}
