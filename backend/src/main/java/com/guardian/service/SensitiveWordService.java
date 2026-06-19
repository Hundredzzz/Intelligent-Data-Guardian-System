package com.guardian.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guardian.common.BusinessException;
import com.guardian.dto.SensitiveWordRequest;
import com.guardian.entity.SensitiveCategory;
import com.guardian.entity.SensitiveWord;
import com.guardian.mapper.SensitiveCategoryMapper;
import com.guardian.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SensitiveWordService {

    private final SensitiveWordMapper sensitiveWordMapper;
    private final SensitiveCategoryMapper sensitiveCategoryMapper;
    private final OperationLogService operationLogService;

    public SensitiveWordService(SensitiveWordMapper sensitiveWordMapper,
                                SensitiveCategoryMapper sensitiveCategoryMapper,
                                OperationLogService operationLogService) {
        this.sensitiveWordMapper = sensitiveWordMapper;
        this.sensitiveCategoryMapper = sensitiveCategoryMapper;
        this.operationLogService = operationLogService;
    }

    public List<SensitiveCategory> categories() {
        return sensitiveCategoryMapper.selectList(new LambdaQueryWrapper<SensitiveCategory>()
                .orderByAsc(SensitiveCategory::getId));
    }

    public List<SensitiveWord> list(String keyword) {
        LambdaQueryWrapper<SensitiveWord> wrapper = new LambdaQueryWrapper<SensitiveWord>()
                .orderByDesc(SensitiveWord::getCreateTime);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(SensitiveWord::getWord, keyword);
        }
        return sensitiveWordMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(SensitiveWordRequest request) {
        if (sensitiveCategoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BusinessException("敏感词分类不存在");
        }
        SensitiveWord word = new SensitiveWord();
        word.setId(request.getId());
        word.setCategoryId(request.getCategoryId());
        word.setWord(request.getWord());
        word.setRiskLevel(request.getRiskLevel());
        word.setEnabled(request.getEnabled());
        if (request.getId() == null) {
            sensitiveWordMapper.insert(word);
        } else {
            sensitiveWordMapper.updateById(word);
        }
        operationLogService.record("SENSITIVE_WORD", "保存敏感词", "敏感词：" + request.getWord() + "，风险：" + request.getRiskLevel());
    }

    public void delete(Long id) {
        sensitiveWordMapper.deleteById(id);
        operationLogService.record("SENSITIVE_WORD", "删除敏感词", "敏感词ID：" + id);
    }
}
