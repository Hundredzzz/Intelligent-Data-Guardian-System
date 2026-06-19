package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.service.DashboardService;
import com.guardian.vo.DashboardStatsVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<DashboardStatsVO> stats() {
        return ApiResponse.success(dashboardService.stats());
    }

    @GetMapping("/risk-distribution")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Object> riskDistribution() {
        return ApiResponse.success(dashboardService.riskDistribution());
    }
}
