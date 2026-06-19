package com.guardian.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetectionHitVO {

    private String type;
    private String value;
    private String riskLevel;
}
