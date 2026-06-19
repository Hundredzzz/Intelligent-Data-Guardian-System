package com.guardian.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.entity.SysUser;
import com.guardian.mapper.SysUserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SysUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createUserIfMissing("admin", "系统管理员", "13800000000", "admin@example.com", 1L, "ADMIN");
        createUserIfMissing("employee", "普通员工", "13800000001", "employee@example.com", 2L, "EMPLOYEE");
        createUserIfMissing("manager", "部门主管", "13800000002", "manager@example.com", 2L, "MANAGER");
        createUserIfMissing("security", "数据安全员", "13800000003", "security@example.com", 1L, "SECURITY_OFFICER");
    }

    private void createUserIfMissing(String username,
                                     String realName,
                                     String phone,
                                     String email,
                                     Long departmentId,
                                     String roleCode) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (count > 0) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setDepartmentId(departmentId);
        user.setRoleCode(roleCode);
        user.setStatus(1);
        userMapper.insert(user);
    }
}
