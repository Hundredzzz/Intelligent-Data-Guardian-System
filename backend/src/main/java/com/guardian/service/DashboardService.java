package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.entity.AuditTask;
import com.guardian.entity.DetectResult;
import com.guardian.mapper.AuditTaskMapper;
import com.guardian.mapper.DetectResultMapper;
import com.guardian.mapper.DetectTaskMapper;
import com.guardian.mapper.SensitiveWordMapper;
import com.guardian.mapper.SysUserMapper;
import com.guardian.mapper.WarningRecordMapper;
import com.guardian.vo.DashboardStatsVO;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final SysUserMapper userMapper;
    private final DetectTaskMapper detectTaskMapper;
    private final DetectResultMapper detectResultMapper;
    private final AuditTaskMapper auditTaskMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final SensitiveWordMapper sensitiveWordMapper;

    public DashboardService(SysUserMapper userMapper,
                            DetectTaskMapper detectTaskMapper,
                            DetectResultMapper detectResultMapper,
                            AuditTaskMapper auditTaskMapper,
                            WarningRecordMapper warningRecordMapper,
                            SensitiveWordMapper sensitiveWordMapper) {
        this.userMapper = userMapper;
        this.detectTaskMapper = detectTaskMapper;
        this.detectResultMapper = detectResultMapper;
        this.auditTaskMapper = auditTaskMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.sensitiveWordMapper = sensitiveWordMapper;
    }

    public DashboardStatsVO stats() {
        long completedAudits = auditTaskMapper.selectCount(new LambdaQueryWrapper<AuditTask>()
                .in(AuditTask::getStatus, "APPROVED", "REJECTED"));
        long approvedAudits = auditTaskMapper.selectCount(new LambdaQueryWrapper<AuditTask>()
                .eq(AuditTask::getStatus, "APPROVED"));

        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setUserCount(userMapper.selectCount(null));
        vo.setDetectCount(detectTaskMapper.selectCount(null));
        vo.setRiskEventCount(warningRecordMapper.selectCount(null));
        vo.setSensitiveWordCount(sensitiveWordMapper.selectCount(null));
        vo.setAuditPassRate(completedAudits == 0 ? 0D : approvedAudits * 100D / completedAudits);
        return vo;
    }

    public Object riskDistribution() {
        return detectResultMapper.selectList(new LambdaQueryWrapper<DetectResult>()
                .select(DetectResult::getRiskLevel, DetectResult::getRiskScore));
    }
}
