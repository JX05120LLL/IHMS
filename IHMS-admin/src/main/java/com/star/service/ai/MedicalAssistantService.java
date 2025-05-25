package com.star.service.ai;

import com.star.model.dto.ChatMessage;
import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.model.entity.UserHealth;
import com.star.service.ILLMService;
import com.star.service.IUserHealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医疗咨询助手服务
 * 提供AI医疗咨询和健康数据分析功能
 */
@Slf4j
@Service
public class MedicalAssistantService {

    @Autowired
    @Qualifier("deepseekService")
    private ILLMService llmService;
    
    @Autowired
    private IUserHealthService userHealthService;
    
    /**
     * 获取医疗健康建议
     * @param request LLM请求对象
     * @return LLM响应对象
     */
    public LLMResponse getMedicalAdvice(LLMRequest request) {
        // 如果没有系统提示词，添加默认的医疗咨询系统提示词
        if (!StringUtils.hasText(request.getSystemPrompt())) {
            request.setSystemPrompt(getDefaultMedicalSystemPrompt());
        }
        
        // 调用DeepSeek获取回复
        return llmService.generateResponse(request);
    }
    
    /**
     * 分析用户健康数据
     * @param userId 用户ID
     * @return 分析结果
     */
    public LLMResponse analyzeUserHealthData(Integer userId) {
        // 获取用户健康数据
        List<UserHealth> healthData = userHealthService.getUserHealthRecords(userId);
        
        if (healthData == null || healthData.isEmpty()) {
            return LLMResponse.builder()
                    .content("没有找到该用户的健康数据记录")
                    .success(true)
                    .build();
        }
        
        // 构建健康数据提示词
        String healthDataPrompt = buildHealthDataPrompt(healthData);
        
        // 构建LLM请求
        LLMRequest request = LLMRequest.builder()
                .systemPrompt(getHealthAnalysisSystemPrompt())
                .userPrompt(healthDataPrompt)
                .temperature(0.3f)  // 降低温度以获得更确定的回答
                .build();
        
        // 获取分析结果
        return llmService.generateResponse(request);
    }
    
    /**
     * 创建健康数据分享消息
     * @param userId 用户ID
     * @param consultationId 咨询ID
     * @param dataTypes 数据类型列表
     * @return 聊天消息对象
     */
    public ChatMessage createHealthDataShareMessage(Integer userId, Integer consultationId, List<String> dataTypes) {
        // 获取指定类型的健康数据
        Map<String, Object> healthData = userHealthService.getSpecificHealthData(userId, dataTypes);
        
        // 创建消息对象
        ChatMessage message = new ChatMessage();
        message.setSenderId(userId);
        message.setConsultationId(consultationId);
        message.setSenderType(0);  // 0表示患者
        message.setMessageType(2);  // 2表示健康数据
        message.setContent("分享了健康数据");
        message.setHealthData(healthData);
        message.setCreateTime(new Date());
        
        return message;
    }
    
    /**
     * 获取默认的医疗咨询系统提示词
     */
    private String getDefaultMedicalSystemPrompt() {
        return "你是一个专业的医疗健康助手，名为\"IHMS医疗顾问\"。" +
                "你可以提供健康知识普及、生活方式建议和初步健康咨询。" +
                "请注意以下限制：\n" +
                "1. 你不是医生，不能提供确切的诊断、处方或治疗方案\n" +
                "2. 对于严重的健康问题，你应该建议用户寻求专业医疗帮助\n" +
                "3. 你应该以友好、专业的语气回答问题\n" +
                "4. 回答应当准确、简洁，并基于可靠的医学知识\n" +
                "5. 如果不确定，应明确告知用户并建议咨询医生\n" +
                "你的目标是帮助用户了解基本健康知识，培养健康的生活习惯。";
    }
    
    /**
     * 获取健康数据分析系统提示词
     */
    private String getHealthAnalysisSystemPrompt() {
        return "你是一个专业的健康数据分析助手。" +
                "你将基于用户的健康数据，提供以下分析：\n" +
                "1. 数据总体评估：分析数据是否在正常范围内\n" +
                "2. 潜在风险提示：指出可能存在的健康风险，但不做确切诊断\n" +
                "3. 改善建议：提供改善健康指标的生活方式和饮食建议\n" +
                "4. 跟踪建议：建议用户应该重点关注哪些指标\n\n" +
                "请注意：你的分析不构成医疗诊断，对于异常数据，应建议用户咨询医生。" +
                "回答应专业、简洁，并基于科学依据。";
    }
    
    /**
     * 构建健康数据提示词
     * @param healthData 健康数据列表
     * @return 格式化的提示词
     */
    private String buildHealthDataPrompt(List<UserHealth> healthData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下健康数据并给出建议：\n\n");
        
        // 添加健康数据信息
        for (UserHealth health : healthData) {
            prompt.append("- 测量时间: ").append(health.getMeasureTime()).append("\n");
            
            if (health.getHeight() != null) {
                prompt.append("  身高: ").append(health.getHeight()).append(" cm\n");
            }
            
            if (health.getWeight() != null) {
                prompt.append("  体重: ").append(health.getWeight()).append(" kg\n");
            }
            
            if (health.getBmi() != null) {
                prompt.append("  BMI: ").append(health.getBmi()).append("\n");
            }
            
            if (health.getSystolicPressure() != null && health.getDiastolicPressure() != null) {
                prompt.append("  血压: ").append(health.getSystolicPressure()).append("/")
                      .append(health.getDiastolicPressure()).append(" mmHg\n");
            }
            
            if (health.getHeartRate() != null) {
                prompt.append("  心率: ").append(health.getHeartRate()).append(" bpm\n");
            }
            
            if (health.getBloodSugar() != null) {
                prompt.append("  血糖: ").append(health.getBloodSugar()).append(" mmol/L\n");
            }
            
            if (health.getBloodOxygen() != null) {
                prompt.append("  血氧饱和度: ").append(health.getBloodOxygen()).append("%\n");
            }
            
            prompt.append("\n");
        }
        
        prompt.append("请提供健康评估和改善建议，包括生活方式、饮食和运动方面的建议。");
        return prompt.toString();
    }
} 