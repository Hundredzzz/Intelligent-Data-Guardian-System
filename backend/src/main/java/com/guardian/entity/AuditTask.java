package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("audit_task")
@EqualsAndHashCode(callSuper = true)
public class AuditTask extends BaseEntity {

    private Long detectTaskId;
    private Long reviewerId;
    private String status;
    private String auditOpinion;
}
