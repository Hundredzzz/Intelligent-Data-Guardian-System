package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("detect_task")
@EqualsAndHashCode(callSuper = true)
public class DetectTask extends BaseEntity {

    private Long userId;
    private String taskName;
    private String contentType;
    private String sourceContent;
    private String status;
}
