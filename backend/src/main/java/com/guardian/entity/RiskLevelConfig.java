package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("risk_level_config")
@EqualsAndHashCode(callSuper = true)
public class RiskLevelConfig extends BaseEntity {

    private String levelCode;
    private String levelName;
    private Integer minScore;
    private Integer maxScore;
    private String handleRule;
    private Integer enabled;
}
