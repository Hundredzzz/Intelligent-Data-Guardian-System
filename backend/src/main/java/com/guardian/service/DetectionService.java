package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.common.RiskLevel;
import com.guardian.dto.DetectRequest;
import com.guardian.entity.AuditTask;
import com.guardian.entity.DetectResult;
import com.guardian.entity.DetectTask;
import com.guardian.entity.SensitiveWord;
import com.guardian.entity.WarningRecord;
import com.guardian.mapper.AuditTaskMapper;
import com.guardian.mapper.DetectResultMapper;
import com.guardian.mapper.DetectTaskMapper;
import com.guardian.mapper.SensitiveWordMapper;
import com.guardian.mapper.WarningRecordMapper;
import com.guardian.security.SecurityContext;
import com.guardian.vo.DetectionHitVO;
import com.guardian.vo.DetectionHistoryVO;
import com.guardian.vo.DetectionResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;

@Service
public class DetectionService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(?<!\\d)1[3-9]\\d{9}(?!\\d)");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(?<!\\d)\\d{17}[\\dXx](?!\\w)");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(?<!\\d)[34569]\\d{15,18}(?!\\d)");
    private static final Pattern BANK_CARD_LABEL_PATTERN = Pattern.compile("(?:收款银行卡号|银行卡号|银行账号|银行卡|卡号)[:：\\s]*([34569]\\d{15,18})(?!\\d)");
    private static final Pattern ADDRESS_LABEL_PATTERN = Pattern.compile("(?:联系地址|通讯地址|收货地址|家庭住址|地址|住址)[:：\\s]*([\\u4e00-\\u9fa5A-Za-z0-9\\-]{4,40})");
    private static final Pattern STRICT_ADDRESS_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]{2,}(?:省|市|自治区)[\\u4e00-\\u9fa5]{1,}(?:区|县)[\\u4e00-\\u9fa5A-Za-z0-9\\-]{2,}(?:路|街|街道|巷|弄|号|栋|单元|室|村)");
    private static final Pattern SECRET_PATTERN = Pattern.compile("(?i)((?:接口密钥|密钥|api[_ -]?key|secret|token)\\s*(?:为|是|=|:|：)?\\s*)([A-Za-z0-9._\\-]{8,})");
    private static final int[] ID_CARD_WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] ID_CARD_CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private final DetectTaskMapper detectTaskMapper;
    private final DetectResultMapper detectResultMapper;
    private final AuditTaskMapper auditTaskMapper;
    private final WarningRecordMapper warningRecordMapper;
    private final SensitiveWordMapper sensitiveWordMapper;
    private final AiReviewService aiReviewService;
    private final FileTextExtractor fileTextExtractor;
    private final OperationLogService operationLogService;

    public DetectionService(DetectTaskMapper detectTaskMapper,
                            DetectResultMapper detectResultMapper,
                            AuditTaskMapper auditTaskMapper,
                            WarningRecordMapper warningRecordMapper,
                            SensitiveWordMapper sensitiveWordMapper,
                            AiReviewService aiReviewService,
                            FileTextExtractor fileTextExtractor,
                            OperationLogService operationLogService) {
        this.detectTaskMapper = detectTaskMapper;
        this.detectResultMapper = detectResultMapper;
        this.auditTaskMapper = auditTaskMapper;
        this.warningRecordMapper = warningRecordMapper;
        this.sensitiveWordMapper = sensitiveWordMapper;
        this.aiReviewService = aiReviewService;
        this.fileTextExtractor = fileTextExtractor;
        this.operationLogService = operationLogService;
    }

    @Transactional(rollbackFor = Exception.class)
    public DetectionResultVO detectText(DetectRequest request) {
        return detect(request.getTaskName(), request.getContent(), "TEXT");
    }

    @Transactional(rollbackFor = Exception.class)
    public DetectionResultVO detectFile(String taskName, MultipartFile file) {
        return detect(taskName, fileTextExtractor.extract(file), "FILE");
    }

    private DetectionResultVO detect(String taskName, String content, String contentType) {
        List<DetectionHitVO> hits = new ArrayList<>();
        List<SensitiveWord> sensitiveWords = enabledSensitiveWords();
        findByPattern(content, PHONE_PATTERN, "手机号", RiskLevel.MEDIUM, hits);
        findByPattern(content, ID_CARD_PATTERN, "身份证号", RiskLevel.HIGH, hits, DetectionService::isValidChineseIdCard);
        findByPattern(content, EMAIL_PATTERN, "邮箱", RiskLevel.LOW, hits);
        findBankCards(content, hits);
        findAddresses(content, hits);
        findSecrets(content, hits);
        findSensitiveWords(content, sensitiveWords, hits);

        int score = calculateRiskScore(hits);
        RiskLevel riskLevel = RiskLevel.fromScore(score);
        String hitSummary = buildHitSummary(hits);
        String desensitizedContent = desensitize(content, sensitiveWords);

        DetectTask task = new DetectTask();
        task.setUserId(SecurityContext.userId());
        task.setTaskName(taskName);
        task.setContentType(contentType);
        task.setSourceContent(content);
        task.setStatus("WAIT_AUDIT");
        detectTaskMapper.insert(task);

        DetectResult result = new DetectResult();
        result.setTaskId(task.getId());
        result.setRiskLevel(riskLevel.name());
        result.setRiskScore(score);
        result.setHitSummary(hitSummary);
        result.setDesensitizedContent(desensitizedContent);
        result.setAiSuggestion(aiReviewService.extractSuggestion(aiReviewService.review(desensitizedContent, riskLevel, hitSummary)));
        detectResultMapper.insert(result);

        createAuditTask(task);
        createWarningIfNeeded(task, riskLevel, hitSummary);
        operationLogService.record("DETECT", "提交检测任务", "提交" + contentType + "检测任务：" + taskName + "，风险：" + riskLevel.name());

        DetectionResultVO vo = new DetectionResultVO();
        vo.setTaskId(task.getId());
        vo.setResultId(result.getId());
        vo.setRiskLevel(riskLevel.getLabel());
        vo.setRiskScore(score);
        vo.setHitSummary(hitSummary);
        vo.setDesensitizedContent(result.getDesensitizedContent());
        vo.setAiSuggestion(result.getAiSuggestion());
        vo.setHits(hits);
        return vo;
    }

    public List<DetectionHistoryVO> history() {
        List<DetectTask> tasks = detectTaskMapper.selectList(new LambdaQueryWrapper<DetectTask>()
                .eq(DetectTask::getUserId, SecurityContext.userId())
                .orderByDesc(DetectTask::getCreateTime));
        return tasks.stream().map(this::toHistory).toList();
    }

    public List<DetectResult> results() {
        return detectResultMapper.selectList(new LambdaQueryWrapper<DetectResult>()
                .orderByDesc(DetectResult::getCreateTime));
    }

    private void findByPattern(String content, Pattern pattern, String type, RiskLevel riskLevel, List<DetectionHitVO> hits) {
        findByPattern(content, pattern, type, riskLevel, hits, value -> true);
    }

    private void findByPattern(String content, Pattern pattern, String type, RiskLevel riskLevel,
                               List<DetectionHitVO> hits, Predicate<String> validator) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String value = matcher.group();
            if (validator.test(value)) {
                hits.add(new DetectionHitVO(type, maskHitValue(type, value), riskLevel.name()));
            }
        }
    }

    private List<SensitiveWord> enabledSensitiveWords() {
        return sensitiveWordMapper.selectList(new LambdaQueryWrapper<SensitiveWord>()
                .eq(SensitiveWord::getEnabled, 1));
    }

    private void findSensitiveWords(String content, List<SensitiveWord> words, List<DetectionHitVO> hits) {
        for (SensitiveWord word : words) {
            if (content.contains(word.getWord())) {
                hits.add(new DetectionHitVO("敏感词", word.getWord(), word.getRiskLevel()));
            }
        }
    }

    private void findBankCards(String content, List<DetectionHitVO> hits) {
        Set<String> matched = new HashSet<>();
        Matcher labelMatcher = BANK_CARD_LABEL_PATTERN.matcher(content);
        while (labelMatcher.find()) {
            String value = labelMatcher.group(1);
            if (!isValidChineseIdCard(value) && matched.add(value)) {
                hits.add(new DetectionHitVO("银行卡号", maskMiddle(value, 4, 4), RiskLevel.CRITICAL.name()));
            }
        }

        Matcher matcher = BANK_CARD_PATTERN.matcher(content);
        while (matcher.find()) {
            String value = matcher.group();
            if (isProbableBankCard(value) && matched.add(value)) {
                hits.add(new DetectionHitVO("银行卡号", maskMiddle(value, 4, 4), RiskLevel.CRITICAL.name()));
            }
        }
    }

    private void findSecrets(String content, List<DetectionHitVO> hits) {
        Matcher matcher = SECRET_PATTERN.matcher(content);
        while (matcher.find()) {
            hits.add(new DetectionHitVO("接口密钥", "********", RiskLevel.CRITICAL.name()));
        }
    }

    private void findAddresses(String content, List<DetectionHitVO> hits) {
        Matcher labelMatcher = ADDRESS_LABEL_PATTERN.matcher(content);
        while (labelMatcher.find()) {
            String value = trimAddressValue(labelMatcher.group(1));
            if (isAddressLike(value)) {
                hits.add(new DetectionHitVO("联系地址", maskAddress(value), RiskLevel.MEDIUM.name()));
            }
        }

        Matcher strictMatcher = STRICT_ADDRESS_PATTERN.matcher(content);
        while (strictMatcher.find()) {
            String value = trimAddressValue(strictMatcher.group());
            if (isAddressLike(value)) {
                hits.add(new DetectionHitVO("联系地址", maskAddress(value), RiskLevel.MEDIUM.name()));
            }
        }
    }

    /**
     * 风险评分采用命中项累加并封顶的方式，便于课程设计中解释规则来源和扩展方式。
     */
    private int calculateRiskScore(List<DetectionHitVO> hits) {
        int score = 0;
        for (DetectionHitVO hit : hits) {
            RiskLevel level = RiskLevel.valueOf(hit.getRiskLevel());
            score += switch (level) {
                case LOW -> 10;
                case MEDIUM -> 20;
                case HIGH -> 35;
                case CRITICAL -> 50;
            };
        }
        return Math.min(score, 100);
    }

    private String buildHitSummary(List<DetectionHitVO> hits) {
        if (hits.isEmpty()) {
            return "未命中敏感信息";
        }
        return hits.stream()
                .map(hit -> hit.getType() + ":" + hit.getValue())
                .distinct()
                .reduce((left, right) -> left + "；" + right)
                .orElse("未命中敏感信息");
    }

    private String desensitize(String content, List<SensitiveWord> sensitiveWords) {
        String masked = PHONE_PATTERN.matcher(content).replaceAll(match -> maskMiddle(match.group(), 3, 4));
        masked = ID_CARD_PATTERN.matcher(masked).replaceAll(match -> isValidChineseIdCard(match.group())
                ? maskMiddle(match.group(), 6, 4)
                : match.group());
        masked = EMAIL_PATTERN.matcher(masked).replaceAll(match -> maskEmail(match.group()));
        masked = maskBankCards(masked);
        masked = SECRET_PATTERN.matcher(masked).replaceAll(match -> match.group(1) + "********");
        for (SensitiveWord word : sensitiveWords) {
            masked = masked.replace(word.getWord(), "***");
        }
        return maskAddresses(masked);
    }

    private String maskMiddle(String text, int prefixLength, int suffixLength) {
        if (text.length() <= prefixLength + suffixLength) {
            return "***";
        }
        return text.substring(0, prefixLength) + "****" + text.substring(text.length() - suffixLength);
    }

    private String maskHitValue(String type, String value) {
        return switch (type) {
            case "手机号" -> maskMiddle(value, 3, 4);
            case "身份证号" -> maskMiddle(value, 6, 4);
            case "邮箱" -> maskEmail(value);
            case "银行卡号" -> maskMiddle(value, 4, 4);
            default -> value;
        };
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***@***";
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private String maskAddresses(String content) {
        String masked = replaceAddressMatches(content, ADDRESS_LABEL_PATTERN, true);
        return replaceAddressMatches(masked, STRICT_ADDRESS_PATTERN, false);
    }

    private String maskBankCards(String content) {
        String masked = BANK_CARD_LABEL_PATTERN.matcher(content).replaceAll(match -> {
            String value = match.group(1);
            if (isValidChineseIdCard(value)) {
                return match.group();
            }
            return match.group().replace(value, maskMiddle(value, 4, 4));
        });
        return BANK_CARD_PATTERN.matcher(masked).replaceAll(match -> isProbableBankCard(match.group())
                ? maskMiddle(match.group(), 4, 4)
                : match.group());
    }

    private String replaceAddressMatches(String content, Pattern pattern, boolean hasLabelGroup) {
        Matcher matcher = pattern.matcher(content);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String value = trimAddressValue(hasLabelGroup ? matcher.group(1) : matcher.group());
            if (!isAddressLike(value)) {
                matcher.appendReplacement(builder, Matcher.quoteReplacement(matcher.group()));
                continue;
            }
            String replacement = hasLabelGroup
                    ? matcher.group().replace(value, maskAddress(value))
                    : maskAddress(value);
            matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    private String trimAddressValue(String value) {
        return value.replaceAll("[，。；;、\\s].*$", "");
    }

    private boolean isAddressLike(String value) {
        if (value.contains("手机号") || value.contains("身份证号") || value.contains("银行卡号")
                || value.contains("邮箱") || value.contains("电话") || value.contains("确认")
                || value.contains("是否") || value.contains("需要") || value.contains("发布")
                || value.contains("同事")) {
            return false;
        }
        int score = 0;
        for (String token : List.of("省", "市", "区", "县", "镇", "乡", "街道", "路", "街", "巷", "弄", "号", "栋", "单元", "室", "村")) {
            if (value.contains(token)) {
                score++;
            }
        }
        return value.length() >= 6 && score >= 2;
    }

    private String maskAddress(String value) {
        if (value.length() <= 6) {
            return "***";
        }
        return value.substring(0, Math.min(3, value.length())) + "****" + value.substring(value.length() - 2);
    }

    private static boolean isValidChineseIdCard(String value) {
        if (!ID_CARD_PATTERN.matcher(value).matches()) {
            return false;
        }
        try {
            int year = Integer.parseInt(value.substring(6, 10));
            int month = Integer.parseInt(value.substring(10, 12));
            int day = Integer.parseInt(value.substring(12, 14));
            java.time.LocalDate.of(year, month, day);
        } catch (Exception ex) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < ID_CARD_WEIGHTS.length; i++) {
            sum += Character.digit(value.charAt(i), 10) * ID_CARD_WEIGHTS[i];
        }
        char expected = ID_CARD_CHECK_CODES[sum % 11];
        return Character.toUpperCase(value.charAt(17)) == expected;
    }

    private static boolean isProbableBankCard(String value) {
        return !isValidChineseIdCard(value) && passesLuhn(value);
    }

    private static boolean passesLuhn(String value) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = value.length() - 1; i >= 0; i--) {
            int digit = Character.digit(value.charAt(i), 10);
            if (digit < 0) {
                return false;
            }
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    private void createAuditTask(DetectTask task) {
        AuditTask auditTask = new AuditTask();
        auditTask.setDetectTaskId(task.getId());
        auditTask.setStatus("PENDING");
        auditTaskMapper.insert(auditTask);
    }

    private void createWarningIfNeeded(DetectTask task, RiskLevel riskLevel, String hitSummary) {
        if (riskLevel != RiskLevel.HIGH && riskLevel != RiskLevel.CRITICAL) {
            return;
        }
        WarningRecord warning = new WarningRecord();
        warning.setDetectTaskId(task.getId());
        warning.setWarningLevel(riskLevel.name());
        warning.setWarningContent("任务“" + task.getTaskName() + "”触发预警：" + hitSummary);
        warning.setStatus("UNHANDLED");
        warningRecordMapper.insert(warning);
        operationLogService.record("WARNING", "生成风险预警", "任务 " + task.getTaskName() + " 触发 " + riskLevel.name() + " 预警");
    }

    private DetectionHistoryVO toHistory(DetectTask task) {
        DetectResult result = detectResultMapper.selectOne(new LambdaQueryWrapper<DetectResult>()
                .eq(DetectResult::getTaskId, task.getId())
                .last("limit 1"));
        AuditTask auditTask = auditTaskMapper.selectOne(new LambdaQueryWrapper<AuditTask>()
                .eq(AuditTask::getDetectTaskId, task.getId())
                .last("limit 1"));

        DetectionHistoryVO vo = new DetectionHistoryVO();
        vo.setTaskId(task.getId());
        vo.setTaskName(task.getTaskName());
        vo.setContentType(task.getContentType());
        vo.setTaskStatus(task.getStatus());
        vo.setCreateTime(task.getCreateTime());
        if (result != null) {
            vo.setRiskLevel(result.getRiskLevel());
            vo.setRiskScore(result.getRiskScore());
            vo.setHitSummary(result.getHitSummary());
            vo.setAiSuggestion(result.getAiSuggestion());
        }
        if (auditTask != null) {
            vo.setAuditStatus(auditTask.getStatus());
            vo.setAuditOpinion(auditTask.getAuditOpinion());
        }
        return vo;
    }
}
