package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("warning_record")
@EqualsAndHashCode(callSuper = true)
public class WarningRecord extends BaseEntity {

    private Long detectTaskId;
    private String warningLevel;
    private String warningContent;
    private String status;
    private Long receiverId;
}
