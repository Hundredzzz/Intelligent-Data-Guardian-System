package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.common.BusinessException;
import com.guardian.entity.WarningRecord;
import com.guardian.mapper.WarningRecordMapper;
import com.guardian.security.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarningService {

    private final WarningRecordMapper warningRecordMapper;
    private final OperationLogService operationLogService;

    public WarningService(WarningRecordMapper warningRecordMapper, OperationLogService operationLogService) {
        this.warningRecordMapper = warningRecordMapper;
        this.operationLogService = operationLogService;
    }

    public List<WarningRecord> list() {
        return warningRecordMapper.selectList(new LambdaQueryWrapper<WarningRecord>()
                .orderByDesc(WarningRecord::getCreateTime));
    }

    public void handle(Long id) {
        WarningRecord warning = warningRecordMapper.selectById(id);
        if (warning == null) {
            throw new BusinessException("预警记录不存在");
        }
        warning.setStatus("HANDLED");
        warning.setReceiverId(SecurityContext.userId());
        warningRecordMapper.updateById(warning);
        operationLogService.record("WARNING", "处理风险预警", "处理预警记录 " + id);
    }
}
