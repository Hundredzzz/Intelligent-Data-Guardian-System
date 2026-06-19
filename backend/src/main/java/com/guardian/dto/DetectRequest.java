package com.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DetectRequest {

    @NotBlank(message = "不能为空")
    private String taskName;

    @NotBlank(message = "不能为空")
    private String content;
}
