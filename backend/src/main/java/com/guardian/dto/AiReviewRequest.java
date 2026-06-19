package com.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiReviewRequest {

    @NotBlank(message = "不能为空")
    private String content;

    private String reviewScene;
    private String dataType;
    private String publishScope;
}
