package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sys_department")
@EqualsAndHashCode(callSuper = true)
public class SysDepartment extends BaseEntity {

    private String departmentName;
    private Long parentId;
}
