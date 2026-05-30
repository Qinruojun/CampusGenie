package com.genie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotQuestion {
    private Long id;
    private String normalizedQuestion;
    private String normalizedAnswer;
    private String displayQuestion;
    private Integer queryCount;
    private LocalDate statStartDate;
    private LocalDate statEndDate;
    private Integer rankNo;
    private Integer version;
    private LocalDateTime updatedTime;
}