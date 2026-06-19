package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("system_config")
@EqualsAndHashCode(callSuper = true)
public class SystemConfig extends BaseEntity {

    private String configKey;
    private String configValue;
    private String description;
}
