package com.genie.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 意图识别服务配置
 */
@Component
@ConfigurationProperties(prefix = "genie.intent")
@Data
public class IntentProperties {
    private String serviceUrl;
    private String token;
    private Integer timeout;
    private Threshold threshold = new Threshold();

    @Data
    public static class Threshold {
        private BigDecimal high = new BigDecimal("0.75");
        private BigDecimal low = new BigDecimal("0.50");
    }
}
