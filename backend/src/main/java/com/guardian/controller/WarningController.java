package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.entity.WarningRecord;
import com.guardian.service.WarningService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/warnings")
public class WarningController {

    private final WarningService warningService;

    public WarningController(WarningService warningService) {
        this.warningService = warningService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','SECURITY_OFFICER')")
    public ApiResponse<List<WarningRecord>> list() {
        return ApiResponse.success(warningService.list());
    }

    @PutMapping("/{id}/handle")
    @PreAuthorize("hasAnyRole('MANAGER','SECURITY_OFFICER')")
    public ApiResponse<Void> handle(@PathVariable Long id) {
        warningService.handle(id);
        return ApiResponse.success("预警已处理", null);
    }
}
