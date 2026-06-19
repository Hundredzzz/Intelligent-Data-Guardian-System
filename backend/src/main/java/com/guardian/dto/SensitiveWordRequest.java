package com.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensitiveWordRequest {

    private Long id;

    @NotNull(message = "不能为空")
    private Long categoryId;

    @NotBlank(message = "不能为空")
    private String word;

    @NotBlank(message = "不能为空")
    private String riskLevel;

    private Integer enabled = 1;
}
