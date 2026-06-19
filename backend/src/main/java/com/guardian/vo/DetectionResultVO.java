package com.guardian.vo;

import lombok.Data;

import java.util.List;

@Data
public class DetectionResultVO {

    private Long taskId;
    private Long resultId;
    private String riskLevel;
    private Integer riskScore;
    private String hitSummary;
    private String desensitizedContent;
    private String aiSuggestion;
    private List<DetectionHitVO> hits;
}
