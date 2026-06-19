package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.entity.SysUser;
import com.guardian.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManageService {

    private final SysUserMapper userMapper;
    private final OperationLogService operationLogService;

    public UserManageService(SysUserMapper userMapper, OperationLogService operationLogService) {
        this.userMapper = userMapper;
        this.operationLogService = operationLogService;
    }

    public List<SysUser> list(String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreateTime);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(SysUser::getUsername, keyword).or().like(SysUser::getRealName, keyword);
        }
        List<SysUser> users = userMapper.selectList(wrapper);
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    public void toggleStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
        operationLogService.record("ADMIN", "修改用户状态", "用户ID：" + id + "，状态：" + status);
    }
}
