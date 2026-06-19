package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.common.BusinessException;
import com.guardian.dto.AuditRequest;
import com.guardian.entity.AuditRecord;
import com.guardian.entity.AuditTask;
import com.guardian.entity.DetectResult;
import com.guardian.entity.DetectTask;
import com.guardian.mapper.AuditRecordMapper;
import com.guardian.mapper.AuditTaskMapper;
import com.guardian.mapper.DetectResultMapper;
import com.guardian.mapper.DetectTaskMapper;
import com.guardian.security.SecurityContext;
import com.guardian.vo.AuditTaskDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditTaskMapper auditTaskMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final DetectTaskMapper detectTaskMapper;
    private final DetectResultMapper detectResultMapper;
    private final OperationLogService operationLogService;

    public AuditService(AuditTaskMapper auditTaskMapper,
                        AuditRecordMapper auditRecordMapper,
                        DetectTaskMapper detectTaskMapper,
                        DetectResultMapper detectResultMapper,
                        OperationLogService operationLogService) {
        this.auditTaskMapper = auditTaskMapper;
        this.auditRecordMapper = auditRecordMapper;
        this.detectTaskMapper = detectTaskMapper;
        this.detectResultMapper = detectResultMapper;
        this.operationLogService = operationLogService;
    }

    public List<AuditTask> listTasks() {
        return auditTaskMapper.selectList(new LambdaQueryWrapper<AuditTask>()
                .orderByDesc(AuditTask::getCreateTime));
    }

    public List<AuditTaskDetailVO> listTaskDetails() {
        return listTasks().stream().map(this::toDetail).toList();
    }

    public List<AuditRecord> listRecords() {
        return auditRecordMapper.selectList(new LambdaQueryWrapper<AuditRecord>()
                .orderByDesc(AuditRecord::getCreateTime));
    }

    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditRequest request) {
        AuditTask auditTask = auditTaskMapper.selectById(request.getAuditTaskId());
        if (auditTask == null) {
            throw new BusinessException("审核任务不存在");
        }
        String normalizedStatus = normalizeStatus(request.getStatus());
        auditTask.setReviewerId(SecurityContext.userId());
        auditTask.setStatus(normalizedStatus);
        auditTask.setAuditOpinion(request.getOpinion());
        auditTaskMapper.updateById(auditTask);

        DetectTask detectTask = detectTaskMapper.selectById(auditTask.getDetectTaskId());
        if (detectTask != null) {
            detectTask.setStatus("APPROVED".equals(normalizedStatus) ? "APPROVED" : "REJECTED");
            detectTaskMapper.updateById(detectTask);
        }

        AuditRecord record = new AuditRecord();
        record.setAuditTaskId(auditTask.getId());
        record.setReviewerId(SecurityContext.userId());
        record.setAction(normalizedStatus);
        record.setOpinion(request.getOpinion());
        auditRecordMapper.insert(record);
        operationLogService.record("AUDIT", "审核检测任务", "审核任务 " + auditTask.getId() + " 结果：" + normalizedStatus + "，意见：" + request.getOpinion());
    }

    private String normalizeStatus(String status) {
        if ("APPROVED".equalsIgnoreCase(status) || "PASS".equalsIgnoreCase(status)) {
            return "APPROVED";
        }
        if ("REJECTED".equalsIgnoreCase(status) || "REJECT".equalsIgnoreCase(status)) {
            return "REJECTED";
        }
        throw new BusinessException("审核状态仅支持 APPROVED 或 REJECTED");
    }

    private AuditTaskDetailVO toDetail(AuditTask auditTask) {
        DetectTask detectTask = detectTaskMapper.selectById(auditTask.getDetectTaskId());
        DetectResult detectResult = detectResultMapper.selectOne(new LambdaQueryWrapper<DetectResult>()
                .eq(DetectResult::getTaskId, auditTask.getDetectTaskId())
                .last("limit 1"));

        AuditTaskDetailVO vo = new AuditTaskDetailVO();
        vo.setAuditTaskId(auditTask.getId());
        vo.setAuditStatus(auditTask.getStatus());
        vo.setAuditOpinion(auditTask.getAuditOpinion());
        vo.setDetectTaskId(auditTask.getDetectTaskId());
        vo.setCreateTime(auditTask.getCreateTime());
        if (detectTask != null) {
            vo.setTaskName(detectTask.getTaskName());
            vo.setContentType(detectTask.getContentType());
            vo.setSourceContent(detectTask.getSourceContent());
            vo.setTaskStatus(detectTask.getStatus());
        }
        if (detectResult != null) {
            vo.setRiskLevel(detectResult.getRiskLevel());
            vo.setRiskScore(detectResult.getRiskScore());
            vo.setHitSummary(detectResult.getHitSummary());
            vo.setDesensitizedContent(detectResult.getDesensitizedContent());
            vo.setAiSuggestion(detectResult.getAiSuggestion());
        }
        return vo;
    }
}
