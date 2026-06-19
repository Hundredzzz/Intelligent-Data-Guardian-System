package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sensitive_category")
@EqualsAndHashCode(callSuper = true)
public class SensitiveCategory extends BaseEntity {

    private String categoryName;
    private String description;
}
