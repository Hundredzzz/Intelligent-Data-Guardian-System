package com.guardian.controller;

import com.guardian.common.ApiResponse;
import com.guardian.dto.AiReviewRequest;
import com.guardian.entity.AiReviewRecord;
import com.guardian.service.AiReviewService;
import com.guardian.service.FileTextExtractor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ai-review")
public class AiReviewController {

    private final AiReviewService aiReviewService;
    private final FileTextExtractor fileTextExtractor;

    public AiReviewController(AiReviewService aiReviewService, FileTextExtractor fileTextExtractor) {
        this.aiReviewService = aiReviewService;
        this.fileTextExtractor = fileTextExtractor;
    }

    @PostMapping
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<String> review(@Valid @RequestBody AiReviewRequest request) {
        return ApiResponse.success(aiReviewService.reviewAndRecord(request, "TEXT", null));
    }

    @PostMapping("/file")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<String> reviewFile(@RequestParam String reviewScene,
                                          @RequestParam String dataType,
                                          @RequestParam String publishScope,
                                          @RequestParam MultipartFile file) {
        AiReviewRequest request = new AiReviewRequest();
        request.setReviewScene(reviewScene);
        request.setDataType(dataType);
        request.setPublishScope(publishScope);
        request.setContent(fileTextExtractor.extract(file));
        return ApiResponse.success(aiReviewService.reviewAndRecord(request, "FILE", file.getOriginalFilename()));
    }

    @GetMapping("/records")
    @PreAuthorize("hasRole('SECURITY_OFFICER')")
    public ApiResponse<List<AiReviewRecord>> records() {
        return ApiResponse.success(aiReviewService.records());
    }
}
