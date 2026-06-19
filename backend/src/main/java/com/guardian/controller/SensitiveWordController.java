package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.dto.SensitiveWordRequest;
import com.guardian.entity.SensitiveCategory;
import com.guardian.entity.SensitiveWord;
import com.guardian.service.SensitiveWordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sensitive-words")
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    public SensitiveWordController(SensitiveWordService sensitiveWordService) {
        this.sensitiveWordService = sensitiveWordService;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<List<SensitiveCategory>> categories() {
        return ApiResponse.success(sensitiveWordService.categories());
    }

    @GetMapping
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<List<SensitiveWord>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(sensitiveWordService.list(keyword));
    }

    @PostMapping
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<Void> save(@Valid @RequestBody SensitiveWordRequest request) {
        sensitiveWordService.save(request);
        return ApiResponse.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sensitiveWordService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
