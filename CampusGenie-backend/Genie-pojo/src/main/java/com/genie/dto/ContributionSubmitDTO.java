package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ContributionSubmitDTO {
    @NotBlank @Size(max = 200)
    private String question;
    @NotBlank @Size(max = 5000)
    private String answer;
    private Integer categoryId;
    private String supplement;//补充说明
    @Size(max = 100)
    private String contact;//贡献者的联系方式
}