package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.entity.OperationLog;
import com.guardian.entity.RiskLevelConfig;
import com.guardian.entity.SysPermission;
import com.guardian.entity.SysRole;
import com.guardian.entity.SystemConfig;
import com.guardian.mapper.OperationLogMapper;
import com.guardian.mapper.RiskLevelConfigMapper;
import com.guardian.mapper.SysPermissionMapper;
import com.guardian.mapper.SysRoleMapper;
import com.guardian.mapper.SystemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final OperationLogMapper operationLogMapper;
    private final RiskLevelConfigMapper riskLevelConfigMapper;
    private final OperationLogService operationLogService;

    public AdminService(SysRoleMapper roleMapper,
                        SysPermissionMapper permissionMapper,
                        SystemConfigMapper systemConfigMapper,
                        OperationLogMapper operationLogMapper,
                        RiskLevelConfigMapper riskLevelConfigMapper,
                        OperationLogService operationLogService) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.systemConfigMapper = systemConfigMapper;
        this.operationLogMapper = operationLogMapper;
        this.riskLevelConfigMapper = riskLevelConfigMapper;
        this.operationLogService = operationLogService;
    }

    public List<SysRole> roles() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getId));
    }

    public void saveRole(SysRole role) {
        if (role.getId() == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        operationLogService.record("ADMIN", "保存角色", "角色：" + role.getRoleCode());
    }

    public List<SysPermission> permissions() {
        return permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getId));
    }

    public void savePermission(SysPermission permission) {
        if (permission.getId() == null) {
            permissionMapper.insert(permission);
        } else {
            permissionMapper.updateById(permission);
        }
        operationLogService.record("ADMIN", "保存权限", "权限：" + permission.getPermissionCode());
    }

    public List<SystemConfig> configs() {
        return systemConfigMapper.selectList(new LambdaQueryWrapper<SystemConfig>().orderByAsc(SystemConfig::getId));
    }

    public void saveConfig(SystemConfig config) {
        if (config.getId() == null) {
            systemConfigMapper.insert(config);
        } else {
            systemConfigMapper.updateById(config);
        }
        operationLogService.record("ADMIN", "保存系统配置", "配置：" + config.getConfigKey());
    }

    public List<OperationLog> logs() {
        return operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>().orderByDesc(OperationLog::getCreateTime));
    }

    public List<RiskLevelConfig> riskLevels() {
        return riskLevelConfigMapper.selectList(new LambdaQueryWrapper<RiskLevelConfig>().orderByAsc(RiskLevelConfig::getMinScore));
    }

    public void saveRiskLevel(RiskLevelConfig config) {
        if (config.getId() == null) {
            riskLevelConfigMapper.insert(config);
        } else {
            riskLevelConfigMapper.updateById(config);
        }
        operationLogService.record("RISK_LEVEL", "保存风险等级", "风险等级：" + config.getLevelCode());
    }
}
