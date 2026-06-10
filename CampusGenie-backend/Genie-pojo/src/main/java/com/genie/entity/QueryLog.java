package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryLog {
    private Long id;
    private String queryText;
    private String normalizedQuery;
    private Integer hit;
    private Integer hitPlace;
    private Long knowledgeId;
    private String sessionId;
    private Integer responseTime;
    private LocalDateTime queryTime;
}