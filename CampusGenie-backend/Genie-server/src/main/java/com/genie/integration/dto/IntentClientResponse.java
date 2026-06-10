package com.genie.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class IntentClientResponse {
    @JsonProperty("category_id")
    private Integer categoryId;

    private BigDecimal confidence;

    @JsonProperty("intent_id")
    private Integer intentId;

    @JsonProperty("intent_code")
    private String intentCode;

    @JsonProperty("intent_name")
    private String intentName;

    private String status;
    private Map<String, Object> raw;
}
