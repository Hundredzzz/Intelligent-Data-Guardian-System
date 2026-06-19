package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sensitive_word")
@EqualsAndHashCode(callSuper = true)
public class SensitiveWord extends BaseEntity {

    private Long categoryId;
    private String word;
    private String riskLevel;
    private Integer enabled;
}
