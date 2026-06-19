package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardian.common.RiskLevel;
import com.guardian.config.DeepSeekProperties;
import com.guardian.dto.AiReviewRequest;
import com.guardian.entity.AiReviewRecord;
import com.guardian.mapper.AiReviewRecordMapper;
import com.guardian.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiReviewService {

    private final DeepSeekProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AiReviewRecordMapper aiReviewRecordMapper;

    public AiReviewService(DeepSeekProperties properties,
                           RestClient.Builder builder,
                           ObjectMapper objectMapper,
                           AiReviewRecordMapper aiReviewRecordMapper) {
        this.properties = properties;
        this.restClient = builder.build();
        this.objectMapper = objectMapper;
        this.aiReviewRecordMapper = aiReviewRecordMapper;
    }

    public String review(String content, RiskLevel riskLevel, String hitSummary) {
        AiReviewRequest request = new AiReviewRequest();
        request.setContent(content);
        request.setReviewScene("检测结果复核");
        request.setDataType("自动识别内容");
        request.setPublishScope("内部审核流程");
        return review(request, riskLevel, hitSummary);
    }

    public String directReview(AiReviewRequest request) {
        return review(request, RiskLevel.MEDIUM, "人工提交 AI 审核");
    }

    public String reviewAndRecord(AiReviewRequest request, String contentType, String fileName) {
        String suggestion = extractSuggestion(directReview(request));
        saveRecord(request, contentType, fileName, suggestion);
        return suggestion;
    }

    public List<AiReviewRecord> records() {
        return aiReviewRecordMapper.selectList(new LambdaQueryWrapper<AiReviewRecord>()
                .orderByDesc(AiReviewRecord::getCreateTime));
    }

    public String review(AiReviewRequest request, RiskLevel riskLevel, String hitSummary) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            return localSuggestion(riskLevel, hitSummary);
        }
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", properties.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt()),
                            Map.of("role", "user", "content", userPrompt(request, riskLevel, hitSummary))
                    ),
                    "temperature", 0.2
            );
            return restClient.post()
                    .uri(properties.getApiUrl())
                    .header("Authorization", "Bearer " + properties.getApiKey())
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (Exception ex) {
            return localSuggestion(riskLevel, hitSummary) + "\n\n说明：DeepSeek 调用失败，已使用本地审核建议兜底。";
        }
    }

    public String extractSuggestion(String responseBody) {
        if (!StringUtils.hasText(responseBody) || !responseBody.trim().startsWith("{")) {
            return responseBody;
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            return content.isMissingNode() ? responseBody : content.asText();
        } catch (Exception ex) {
            return responseBody;
        }
    }

    private String systemPrompt() {
        return """
                你是企业数据安全审核助手。请根据内容判断泄露风险，并用中文输出结构化审核结论。
                必须包含：
                1. 风险等级：低风险/中风险/高风险/严重风险
                2. 是否建议发布：允许发布/脱敏后发布/禁止发布
                3. 风险依据：列出命中的个人隐私、商业机密、财务数据、合同内容、技术资料等
                4. 修改建议：给出可执行脱敏或删除建议
                5. 审核结论：给主管或数据安全员的一句话结论
                """;
    }

    private String userPrompt(AiReviewRequest request, RiskLevel riskLevel, String hitSummary) {
        return """
                审核场景：%s
                资料类型：%s
                发布范围：%s
                规则检测风险：%s
                规则命中摘要：%s

                待审核内容：
                %s
                """.formatted(
                defaultText(request.getReviewScene(), "未填写"),
                defaultText(request.getDataType(), "未填写"),
                defaultText(request.getPublishScope(), "未填写"),
                riskLevel.getLabel(),
                hitSummary,
                request.getContent()
        );
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    /**
     * 保存AI审核轨迹，方便数据安全员复盘文本/文件审核结论。
     */
    private void saveRecord(AiReviewRequest request, String contentType, String fileName, String suggestion) {
        AiReviewRecord record = new AiReviewRecord();
        record.setReviewerId(SecurityContext.userId());
        record.setReviewScene(request.getReviewScene());
        record.setDataType(request.getDataType());
        record.setPublishScope(request.getPublishScope());
        record.setContentType(contentType);
        record.setFileName(fileName);
        record.setSourceContent(request.getContent());
        record.setReviewResult(suggestion);
        aiReviewRecordMapper.insert(record);
    }

    private String localSuggestion(RiskLevel riskLevel, String hitSummary) {
        if (riskLevel == RiskLevel.CRITICAL || riskLevel == RiskLevel.HIGH) {
            return "风险等级：高风险\n是否建议发布：禁止发布\n风险依据：" + hitSummary
                    + "\n修改建议：删除或脱敏敏感字段后重新提交。\n审核结论：需要主管和数据安全员复核。";
        }
        if (riskLevel == RiskLevel.MEDIUM) {
            return "风险等级：中风险\n是否建议发布：脱敏后发布\n风险依据：" + hitSummary
                    + "\n修改建议：确认发布范围，对个人信息做部分脱敏。\n审核结论：修改后可进入审核流程。";
        }
        return "风险等级：低风险\n是否建议发布：允许发布\n风险依据：未发现明显敏感信息。"
                + "\n修改建议：保留审核记录。\n审核结论：可按流程发布。";
    }
}
