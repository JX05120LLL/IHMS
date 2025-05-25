package com.star.controller;

import com.star.model.dto.ChatMessage;
import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.service.ILLMService;
import com.star.service.IUserHealthService;
import com.star.service.ai.MedicalAssistantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医疗咨询助手控制器
 */
@Api(tags = "医疗咨询助手接口")
@RestController
@RequestMapping("/api/medical-assistant")
public class MedicalAssistantController {

    @Autowired
    @Qualifier("deepseekService")
    private ILLMService llmService;
    
    @Autowired
    private MedicalAssistantService medicalAssistantService;
    
    @Autowired
    private IUserHealthService userHealthService;

    /**
     * 获取医疗咨询回复
     */
    @ApiOperation("获取医疗咨询回复")
    @PostMapping("/consult")
    public ResponseEntity<LLMResponse> getMedicalConsultation(@RequestBody LLMRequest request) {
        LLMResponse response = medicalAssistantService.getMedicalAdvice(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取健康数据分析
     */
    @ApiOperation("获取用户健康数据分析")
    @GetMapping("/health-analysis/{userId}")
    public ResponseEntity<LLMResponse> getHealthDataAnalysis(
            @ApiParam(value = "用户ID", required = true)
            @PathVariable Integer userId) {
        
        LLMResponse response = medicalAssistantService.analyzeUserHealthData(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 分享健康数据到聊天
     */
    @ApiOperation("分享健康数据到聊天")
    @PostMapping("/share-health-data")
    public ResponseEntity<ChatMessage> shareHealthData(
            @ApiParam(value = "健康数据分享请求", required = true)
            @RequestBody Map<String, Object> shareRequest) {
        
        Integer userId = (Integer) shareRequest.get("userId");
        Integer consultationId = (Integer) shareRequest.get("consultationId");
        List<String> dataTypes = (List<String>) shareRequest.get("dataTypes");
        
        ChatMessage message = medicalAssistantService.createHealthDataShareMessage(
                userId, consultationId, dataTypes);
        
        return ResponseEntity.ok(message);
    }
    
    /**
     * 提供简单健康建议
     */
    @ApiOperation("提供简单健康建议")
    @GetMapping("/quick-advice")
    public ResponseEntity<LLMResponse> getQuickHealthAdvice(
            @ApiParam(value = "健康问题", required = true, example = "如何缓解颈椎疼痛")
            @RequestParam String question) {
        
        LLMRequest request = LLMRequest.builder()
                .systemPrompt("你是一个专业的医疗健康助手，提供简短有用的健康建议。注意：你不是医生，不能提供诊断和处方，严重问题建议就医。")
                .userPrompt(question)
                .temperature(0.3f)
                .build();
        
        LLMResponse response = medicalAssistantService.getMedicalAdvice(request);
        return ResponseEntity.ok(response);
    }
} 