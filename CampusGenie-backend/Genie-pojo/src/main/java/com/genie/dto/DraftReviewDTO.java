package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class DraftReviewDTO {
    @NotNull
    private Long draftId;
    @NotNull
    private Integer action;
    private String rejectReason;
    private String editedQuestion;
    private String editedAnswer;
}
