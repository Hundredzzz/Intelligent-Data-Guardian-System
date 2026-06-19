package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.dto.DetectRequest;
import com.guardian.entity.DetectResult;
import com.guardian.entity.DetectTask;
import com.guardian.service.DetectionService;
import com.guardian.vo.DetectionHistoryVO;
import com.guardian.vo.DetectionResultVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/detect")
public class DetectionController {

    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/text")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<DetectionResultVO> detectText(@Valid @RequestBody DetectRequest request) {
        return ApiResponse.success(detectionService.detectText(request));
    }

    @PostMapping("/file")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<DetectionResultVO> detectFile(@RequestParam String taskName, @RequestParam MultipartFile file) {
        return ApiResponse.success(detectionService.detectFile(taskName, file));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ApiResponse<List<DetectionHistoryVO>> history() {
        return ApiResponse.success(detectionService.history());
    }

    @GetMapping("/results")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<List<DetectResult>> results() {
        return ApiResponse.success(detectionService.results());
    }
}
