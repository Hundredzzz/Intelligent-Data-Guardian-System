package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.dto.ChangePasswordRequest;
import com.guardian.dto.LoginRequest;
import com.guardian.dto.ProfileRequest;
import com.guardian.dto.RegisterRequest;
import com.guardian.entity.SysUser;
import com.guardian.service.AuthService;
import com.guardian.service.CaptchaService;
import com.guardian.vo.CaptchaVO;
import com.guardian.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CaptchaService captchaService;

    public AuthController(AuthService authService, CaptchaService captchaService) {
        this.authService = authService;
        this.captchaService = captchaService;
    }

    @GetMapping("/captcha")
    public ApiResponse<CaptchaVO> captcha() {
        return ApiResponse.success(captchaService.generate());
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("注册成功", null);
    }

    @GetMapping("/profile")
    public ApiResponse<SysUser> profile() {
        return ApiResponse.success(authService.profile());
    }

    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(@Valid @RequestBody ProfileRequest request) {
        authService.updateProfile(request);
        return ApiResponse.success("资料已更新", null);
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("密码已修改", null);
    }
}
