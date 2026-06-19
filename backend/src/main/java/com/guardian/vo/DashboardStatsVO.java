package com.guardian.vo;

import lombok.Data;

@Data
public class DashboardStatsVO {

    private Long userCount;
    private Long detectCount;
    private Long riskEventCount;
    private Double auditPassRate;
    private Long sensitiveWordCount;
}
