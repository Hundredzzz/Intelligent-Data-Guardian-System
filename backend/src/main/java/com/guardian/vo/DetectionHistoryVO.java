package com.guardian.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DetectionHistoryVO {

    private Long taskId;
    private String taskName;
    private String contentType;
    private String taskStatus;
    private String riskLevel;
    private Integer riskScore;
    private String hitSummary;
    private String aiSuggestion;
    private String auditStatus;
    private String auditOpinion;
    private LocalDateTime createTime;
}
