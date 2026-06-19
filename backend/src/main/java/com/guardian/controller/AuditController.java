package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.dto.AuditRequest;
import com.guardian.entity.AuditRecord;
import com.guardian.entity.AuditTask;
import com.guardian.service.AuditService;
import com.guardian.vo.AuditTaskDetailVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<AuditTask>> tasks() {
        return ApiResponse.success(auditService.listTasks());
    }

    @GetMapping("/task-details")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<AuditTaskDetailVO>> taskDetails() {
        return ApiResponse.success(auditService.listTaskDetails());
    }

    @GetMapping("/records")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<List<AuditRecord>> records() {
        return ApiResponse.success(auditService.listRecords());
    }

    @PostMapping("/handle")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> audit(@Valid @RequestBody AuditRequest request) {
        auditService.audit(request);
        return ApiResponse.success("审核完成", null);
    }
}
