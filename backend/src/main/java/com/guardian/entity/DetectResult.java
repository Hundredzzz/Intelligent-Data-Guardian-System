package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("detect_result")
@EqualsAndHashCode(callSuper = true)
public class DetectResult extends BaseEntity {

    private Long taskId;
    private String riskLevel;
    private Integer riskScore;
    private String hitSummary;
    private String desensitizedContent;
    private String aiSuggestion;
}
