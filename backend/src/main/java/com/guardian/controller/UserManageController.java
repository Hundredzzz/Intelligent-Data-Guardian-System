package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.entity.SysUser;
import com.guardian.service.UserManageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserManageController {

    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SysUser>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(userManageService.list(keyword));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        userManageService.toggleStatus(id, status);
        return ApiResponse.success("状态已更新", null);
    }
}
