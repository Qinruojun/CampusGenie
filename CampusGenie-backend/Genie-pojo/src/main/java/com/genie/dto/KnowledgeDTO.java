package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class KnowledgeDTO {
    private Long id;                 // 修改时传，新增时不传
    @NotBlank @Size(max = 200)
    private String question;
    @NotBlank @Size(max = 5000)
    private String answer;
    private Integer categoryId;
    private String source;
    private Integer status;          // 1-发布, 0-停用
}