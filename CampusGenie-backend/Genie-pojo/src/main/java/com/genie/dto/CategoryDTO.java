package com.genie.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryDTO {
    private Integer id;            // 修改时传
    @NotBlank//只修饰name
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
}