package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.common.BusinessException;
import com.guardian.dto.ChangePasswordRequest;
import com.guardian.dto.LoginRequest;
import com.guardian.dto.ProfileRequest;
import com.guardian.dto.RegisterRequest;
import com.guardian.entity.SysUser;
import com.guardian.mapper.SysUserMapper;
import com.guardian.security.JwtTokenProvider;
import com.guardian.security.SecurityContext;
import com.guardian.vo.LoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CaptchaService captchaService;
    private final OperationLogService operationLogService;

    public AuthService(SysUserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       CaptchaService captchaService,
                       OperationLogService operationLogService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.captchaService = captchaService;
        this.operationLogService = operationLogService;
    }

    public LoginVO login(LoginRequest request) {
        captchaService.validate(request.getCaptchaKey(), request.getCaptcha());
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (Integer.valueOf(0).equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被禁用");
        }
        operationLogService.record(user.getId(), "LOGIN", "用户登录", "用户 " + user.getUsername() + " 登录系统");
        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getRoleCode());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRoleCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setDepartmentId(request.getDepartmentId());
        user.setRoleCode("EMPLOYEE");
        user.setStatus(1);
        userMapper.insert(user);
        operationLogService.record(user.getId(), "USER", "用户注册", "注册用户 " + user.getUsername());
    }

    public SysUser profile() {
        SysUser user = userMapper.selectById(SecurityContext.userId());
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    public void updateProfile(ProfileRequest request) {
        SysUser user = new SysUser();
        user.setId(SecurityContext.userId());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        userMapper.updateById(user);
        operationLogService.record("USER", "修改个人资料", "更新个人中心资料");
    }

    public void changePassword(ChangePasswordRequest request) {
        SysUser user = userMapper.selectById(SecurityContext.userId());
        if (user == null || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        operationLogService.record("USER", "修改密码", "用户修改个人密码");
    }
}
