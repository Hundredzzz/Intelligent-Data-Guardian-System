package com.guardian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditRequest {

    @NotNull(message = "不能为空")
    private Long auditTaskId;

    @NotBlank(message = "不能为空")
    private String status;

    private String opinion;
}
