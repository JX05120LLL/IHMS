package com.star.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.mapper.HealthAdviceMapper;
import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.model.entity.HealthAdvice;
import com.star.model.entity.HealthRiskAssessment;
import com.star.service.IHealthAdviceService;
import com.star.service.ILLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于大模型的健康建议服务实现
 */
@Slf4j
@Service
public class LLMHealthAdviceServiceImpl implements IHealthAdviceService {
    
    @Autowired
    @Qualifier("deepseekService")
    private ILLMService llmService;
    
    @Autowired
    private HealthAdviceMapper healthAdviceMapper;
    
    /**
     * 根据健康风险评估生成建议
     */
    @Override
    public List<HealthAdvice> generateAdviceFromRisk(Integer userId, HealthRiskAssessment riskAssessment) {
        // 已有建议则不重复生成
        LambdaQueryWrapper<HealthAdvice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthAdvice::getUserId, userId);
        queryWrapper.gt(HealthAdvice::getCreateTime, LocalDateTime.now().minusDays(7)); // 最近7天的建议
        List<HealthAdvice> existingAdvices = healthAdviceMapper.selectList(queryWrapper);
        
        if (existingAdvices != null && !existingAdvices.isEmpty()) {
            return existingAdvices;
        }
        
        // 构建提示词
        String systemPrompt = "你是一位专业的健康顾问，负责根据用户的健康风险评估结果，提供针对性的健康建议。这些建议应该具体、可行，并分为饮食、运动、生活习惯等不同类别。";
        
        String userPrompt = "请根据以下健康风险评估结果，生成5条具体的健康建议：\n\n" +
                "风险等级: " + riskAssessment.getRiskLevel() + "\n" +
                "风险因素: " + riskAssessment.getRiskFactors() + "\n" +
                "评估结果: " + riskAssessment.getAssessmentResult() + "\n\n" +
                "请按以下格式输出，每条建议需包含类型、内容、优先级(1-5):\n" +
                "1. 类型: [类型]\n   内容: [具体建议]\n   优先级: [1-5]\n" +
                "2. ...";
        
        // 调用大模型
        LLMRequest request = LLMRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(userPrompt)
                .temperature(0.7f) // 适当增加创造性
                .build();
        
        LLMResponse response = llmService.generateResponse(request);
        
        if (!response.getSuccess()) {
            log.error("调用大模型生成健康建议失败: {}", response.getErrorMessage());
            return new ArrayList<>();
        }
        
        // 解析大模型返回的结果
        List<HealthAdvice> advices = parseAdviceResponse(response.getContent(), userId);
        
        // 保存到数据库
        for (HealthAdvice advice : advices) {
            healthAdviceMapper.insert(advice);
        }
        
        return advices;
    }
    
    /**
     * 获取用户的所有健康建议
     */
    @Override
    public List<HealthAdvice> getUserAdvices(Integer userId) {
        LambdaQueryWrapper<HealthAdvice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthAdvice::getUserId, userId);
        queryWrapper.orderByDesc(HealthAdvice::getCreateTime);
        return healthAdviceMapper.selectList(queryWrapper);
    }
    
    /**
     * 标记建议为已读
     */
    @Override
    public boolean markAdviceAsRead(Integer adviceId) {
        HealthAdvice advice = healthAdviceMapper.selectById(adviceId);
        if (advice == null) {
            return false;
        }
        
        advice.setIsRead((byte) 1);
        return healthAdviceMapper.updateById(advice) > 0;
    }
    
    /**
     * 标记建议为已执行
     */
    @Override
    public boolean markAdviceAsImplemented(Integer adviceId) {
        HealthAdvice advice = healthAdviceMapper.selectById(adviceId);
        if (advice == null) {
            return false;
        }
        
        advice.setIsImplemented((byte) 1);
        return healthAdviceMapper.updateById(advice) > 0;
    }
    
    /**
     * 解析建议响应
     */
    private List<HealthAdvice> parseAdviceResponse(String content, Integer userId) {
        List<HealthAdvice> advices = new ArrayList<>();
        
        // 使用正则表达式提取建议
        Pattern pattern = Pattern.compile("\\d+\\.\\s*类型:\\s*([^\\n]+)\\s*\\n\\s*内容:\\s*([^\\n]+)\\s*\\n\\s*优先级:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String adviceType = matcher.group(1).trim();
            String adviceContent = matcher.group(2).trim();
            byte priority = Byte.parseByte(matcher.group(3).trim());
            
            HealthAdvice advice = new HealthAdvice();
            advice.setUserId(userId);
            advice.setAdviceType(adviceType);
            advice.setContent(adviceContent);
            advice.setPriority(priority);
            advice.setSource("AI");
            advice.setCreateTime(LocalDateTime.now());
            advice.setIsRead((byte) 0);
            advice.setIsImplemented((byte) 0);
            
            advices.add(advice);
        }
        
        return advices;
    }
} 