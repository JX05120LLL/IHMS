package com.star.service.impl;

import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.model.entity.HealthRiskAssessment;
import com.star.model.entity.UserHealth;
import com.star.model.entity.UserProfile;
import com.star.service.IHealthAnalysisService;
import com.star.service.ILLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于大模型的健康分析服务实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "health.analysis.provider", havingValue = "llm")
public class LLMHealthAnalysisServiceImpl implements IHealthAnalysisService {
    
    @Autowired
    @Qualifier("deepseekService")
    private ILLMService llmService;
    
    /**
     * 分析用户健康风险
     */
    @Override
    public HealthRiskAssessment analyzeHealthRisk(Integer userId) {
        // 获取用户健康数据
        List<UserHealth> healthData = getUserHealthData(userId);
        
        if (healthData == null || healthData.isEmpty()) {
            log.warn("用户{}没有健康数据记录", userId);
            return null;
        }
        
        // 格式化健康数据
        String healthDataText = formatHealthDataForLLM(healthData);
        
        // 构建提示词
        String systemPrompt = "你是一位专业的健康顾问，负责分析用户健康数据并评估其健康风险。请基于提供的健康数据，" + 
                              "给出全面的健康风险评估，包括总体风险等级（1-5，5最高），主要风险因素，以及评估分析。";
        
        String userPrompt = "请分析以下用户健康数据，并提供健康风险评估：\n\n" + healthDataText + 
                          "\n\n请以JSON格式输出你的分析结果，包含以下字段：\n" + 
                          "1. riskLevel: 整数，范围1-5\n" + 
                          "2. riskFactors: 数组，列出主要风险因素\n" + 
                          "3. assessmentResult: 文本，详细的风险评估分析";
        
        // 调用大模型
        LLMRequest request = LLMRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(userPrompt)
                .temperature(0.2f) // 降低随机性
                .build();
        
        LLMResponse response = llmService.generateResponse(request);
        
        if (!response.getSuccess()) {
            log.error("调用大模型分析健康风险失败: {}", response.getErrorMessage());
            return null;
        }
        
        // 解析大模型返回的结果
        return parseRiskAssessmentResponse(response.getContent(), userId);
    }
    
    /**
     * 生成或更新用户画像
     */
    @Override
    public UserProfile generateUserProfile(Integer userId) {
        // 获取用户信息和健康数据
        // User user = getUserInfo(userId);
        List<UserHealth> healthData = getUserHealthData(userId);
        
        if (healthData == null || healthData.isEmpty()) {
            log.warn("用户{}没有健康数据记录", userId);
            return null;
        }
        
        // 格式化用户信息和健康数据
        String userDataText = formatUserDataForLLM(userId, healthData);
        
        // 构建提示词
        String systemPrompt = "你是一位人工智能健康助手，负责根据用户的健康数据生成用户健康画像。";
        
        String userPrompt = "请根据以下用户信息和健康数据，生成用户健康画像：\n\n" + userDataText +
                          "\n\n请以JSON格式输出，包含以下字段：\n" +
                          "1. healthStatus: 字符串，总体健康状态分类\n" +
                          "2. riskLevel: 整数，健康风险等级(1-5)\n" +
                          "3. activeLevel: 整数，活跃度(1-5)\n" +
                          "4. preferenceTags: 字符串数组，用户可能的健康偏好标签";
        
        // 调用大模型
        LLMRequest request = LLMRequest.builder()
                .systemPrompt(systemPrompt)
                .userPrompt(userPrompt)
                .temperature(0.3f)
                .build();
        
        LLMResponse response = llmService.generateResponse(request);
        
        if (!response.getSuccess()) {
            log.error("调用大模型生成用户画像失败: {}", response.getErrorMessage());
            return null;
        }
        
        // 解析大模型返回的结果
        return parseUserProfileResponse(response.getContent(), userId);
    }
    
    /**
     * 分析用户健康数据异常
     */
    @Override
    public void analyzeHealthAnomalies(Integer userId) {
        // TODO: 实现健康异常分析
    }
    
    /**
     * 预测用户健康指标趋势
     */
    @Override
    public boolean predictHealthTrend(Integer userId, Integer metricType, Integer days) {
        // TODO: 实现健康趋势预测
        return false;
    }
    
    /**
     * 获取用户健康数据
     * 这里需要根据实际项目修改，连接到真实的数据源
     */
    private List<UserHealth> getUserHealthData(Integer userId) {
        // TODO: 实现从数据库获取用户健康数据
        return null;
    }
    
    /**
     * 格式化健康数据为大模型友好的文本
     */
    private String formatHealthDataForLLM(List<UserHealth> healthData) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户健康数据概览：\n");
        
        // 示例逻辑，需根据实际数据结构调整
        for (UserHealth record : healthData) {
            sb.append("- 记录时间: ").append(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
              .append(", 指标类型: ").append(record.getHealthModelConfigId())
              .append(", 数值: ").append(record.getValue())
              .append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 格式化用户数据为大模型友好的文本
     */
    private String formatUserDataForLLM(Integer userId, List<UserHealth> healthData) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("用户ID: ").append(userId).append("\n");
        // 添加更多用户信息...
        
        sb.append("\n健康数据:\n");
        sb.append(formatHealthDataForLLM(healthData));
        
        return sb.toString();
    }
    
    /**
     * 解析风险评估响应
     */
    private HealthRiskAssessment parseRiskAssessmentResponse(String content, Integer userId) {
        try {
            HealthRiskAssessment assessment = new HealthRiskAssessment();
            assessment.setUserId(userId);
            assessment.setAssessmentType("OVERALL_HEALTH");
            assessment.setAssessmentTime(LocalDateTime.now());
            
            // 提取风险等级
            Pattern riskLevelPattern = Pattern.compile("\"riskLevel\"\\s*:\\s*(\\d+)");
            Matcher riskLevelMatcher = riskLevelPattern.matcher(content);
            if (riskLevelMatcher.find()) {
                assessment.setRiskLevel(Byte.parseByte(riskLevelMatcher.group(1)));
            }
            
            // 提取风险因素
            Pattern riskFactorsPattern = Pattern.compile("\"riskFactors\"\\s*:\\s*(\\[.*?\\])");
            Matcher riskFactorsMatcher = riskFactorsPattern.matcher(content);
            if (riskFactorsMatcher.find()) {
                assessment.setRiskFactors(riskFactorsMatcher.group(1));
            }
            
            // 提取评估结果
            Pattern assessmentResultPattern = Pattern.compile("\"assessmentResult\"\\s*:\\s*\"(.*?)\"");
            Matcher assessmentResultMatcher = assessmentResultPattern.matcher(content);
            if (assessmentResultMatcher.find()) {
                assessment.setAssessmentResult(assessmentResultMatcher.group(1));
            }
            
            return assessment;
        } catch (Exception e) {
            log.error("解析风险评估响应失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析用户画像响应
     */
    private UserProfile parseUserProfileResponse(String content, Integer userId) {
        try {
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setLastUpdated(LocalDateTime.now());
            
            // 提取健康状态
            Pattern healthStatusPattern = Pattern.compile("\"healthStatus\"\\s*:\\s*\"(.*?)\"");
            Matcher healthStatusMatcher = healthStatusPattern.matcher(content);
            if (healthStatusMatcher.find()) {
                profile.setHealthStatus(healthStatusMatcher.group(1));
            }
            
            // 提取风险等级
            Pattern riskLevelPattern = Pattern.compile("\"riskLevel\"\\s*:\\s*(\\d+)");
            Matcher riskLevelMatcher = riskLevelPattern.matcher(content);
            if (riskLevelMatcher.find()) {
                profile.setRiskLevel(Byte.parseByte(riskLevelMatcher.group(1)));
            }
            
            // 提取活跃度
            Pattern activeLevelPattern = Pattern.compile("\"activeLevel\"\\s*:\\s*(\\d+)");
            Matcher activeLevelMatcher = activeLevelPattern.matcher(content);
            if (activeLevelMatcher.find()) {
                profile.setActiveLevel(Byte.parseByte(activeLevelMatcher.group(1)));
            }
            
            // 提取偏好标签
            Pattern tagsPattern = Pattern.compile("\"preferenceTags\"\\s*:\\s*(\\[.*?\\])");
            Matcher tagsMatcher = tagsPattern.matcher(content);
            if (tagsMatcher.find()) {
                profile.setPreferenceTags(tagsMatcher.group(1));
            }
            
            return profile;
        } catch (Exception e) {
            log.error("解析用户画像响应失败: {}", e.getMessage());
            return null;
        }
    }
} 