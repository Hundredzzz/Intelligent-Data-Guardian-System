package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.entity.OperationLog;
import com.guardian.entity.RiskLevelConfig;
import com.guardian.entity.SysPermission;
import com.guardian.entity.SysRole;
import com.guardian.entity.SystemConfig;
import com.guardian.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysRole>> roles() {
        return ApiResponse.success(adminService.roles());
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> saveRole(@RequestBody SysRole role) {
        adminService.saveRole(role);
        return ApiResponse.success("保存成功", null);
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysPermission>> permissions() {
        return ApiResponse.success(adminService.permissions());
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> savePermission(@RequestBody SysPermission permission) {
        adminService.savePermission(permission);
        return ApiResponse.success("保存成功", null);
    }

    @GetMapping("/configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SystemConfig>> configs() {
        return ApiResponse.success(adminService.configs());
    }

    @PostMapping("/configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> saveConfig(@RequestBody SystemConfig config) {
        adminService.saveConfig(config);
        return ApiResponse.success("保存成功", null);
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OperationLog>> logs() {
        return ApiResponse.success(adminService.logs());
    }

    @GetMapping("/risk-levels")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<List<RiskLevelConfig>> riskLevels() {
        return ApiResponse.success(adminService.riskLevels());
    }

    @PostMapping("/risk-levels")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<Void> saveRiskLevel(@RequestBody RiskLevelConfig config) {
        adminService.saveRiskLevel(config);
        return ApiResponse.success("保存成功", null);
    }
}
