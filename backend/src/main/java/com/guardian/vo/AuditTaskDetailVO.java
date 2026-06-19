package com.guardian.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditTaskDetailVO {

    private Long auditTaskId;
    private String auditStatus;
    private String auditOpinion;
    private Long detectTaskId;
    private String taskName;
    private String contentType;
    private String sourceContent;
    private String taskStatus;
    private String riskLevel;
    private Integer riskScore;
    private String hitSummary;
    private String desensitizedContent;
    private String aiSuggestion;
    private LocalDateTime createTime;
}
