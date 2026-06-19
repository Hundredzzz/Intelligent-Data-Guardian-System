package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("audit_record")
@EqualsAndHashCode(callSuper = true)
public class AuditRecord extends BaseEntity {

    private Long auditTaskId;
    private Long reviewerId;
    private String action;
    private String opinion;
}
