package com.guardian.service;

import com.guardian.entity.OperationLog;
import com.guardian.mapper.OperationLogMapper;
import com.guardian.security.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogService(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    public void record(String logType, String operation, String detail) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(currentUserId());
            log.setLogType(logType);
            log.setOperation(operation);
            log.setDetail(detail);
            log.setIpAddress(clientIp());
            operationLogMapper.insert(log);
        } catch (Exception ignored) {
            // Logging must not interrupt the main business flow.
        }
    }

    public void record(Long userId, String logType, String operation, String detail) {
        try {
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setLogType(logType);
            log.setOperation(operation);
            log.setDetail(detail);
            log.setIpAddress(clientIp());
            operationLogMapper.insert(log);
        } catch (Exception ignored) {
            // Logging must not interrupt the main business flow.
        }
    }

    private Long currentUserId() {
        try {
            return SecurityContext.userId();
        } catch (Exception ex) {
            return null;
        }
    }

    private String clientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
