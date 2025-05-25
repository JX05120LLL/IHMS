package com.star.controller;

import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.service.ILLMService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Deepseek聊天控制器
 * 提供页面助手与DeepSeek大模型的交互接口
 */
@Api(tags = "DeepSeek聊天接口")
@RestController
@RequestMapping("/api/deepseek")
public class DeepseekChatController {

    @Autowired
    @Qualifier("deepseekService")
    private ILLMService llmService;

    /**
     * 与DeepSeek大模型对话
     * 
     * @param request LLM请求对象
     * @return LLM响应
     */
    @ApiOperation("发送消息到DeepSeek大模型")
    @PostMapping("/chat")
    public ResponseEntity<LLMResponse> chat(@RequestBody LLMRequest request) {
        // 如果没有设置系统提示词，添加默认系统提示词
        if (request.getSystemPrompt() == null || request.getSystemPrompt().trim().isEmpty()) {
            request.setSystemPrompt("你是一个专业的医疗健康助手，名为IHMS医疗顾问。"
                    + "你可以提供健康知识普及、生活方式建议和初步健康咨询。"
                    + "请注意以下限制："
                    + "1. 你不是医生，不能提供确切的诊断、处方或治疗方案。"
                    + "2. 对于严重的健康问题，你应该建议用户寻求专业医疗帮助。"
                    + "3. 你应该以友好、专业的语气回答问题。"
                    + "4. 回答应当准确、简洁，并基于可靠的医学知识。"
                    + "5. 如果不确定，应明确告知用户并建议咨询医生。");
        }
        
        // 检查和清理用户提示词，确保没有特殊字符导致解析错误
        if (request.getUserPrompt() != null) {
            // 清除可能导致JSON解析问题的字符
            String cleanedPrompt = request.getUserPrompt()
                .replace("\t", " ")
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("\"", "'");
            request.setUserPrompt(cleanedPrompt);
        }
        
        // 如果没有设置温度，设置默认温度为0.3
        if (request.getTemperature() == null) {
            request.setTemperature(0.3f);
        }
        
        // 如果没有设置最大令牌数，设置一个默认值
        if (request.getMaxTokens() == null) {
            request.setMaxTokens(2000);
        }
        
        LLMResponse response = llmService.generateResponse(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 简单健康咨询接口
     * 
     * @param question 健康问题
     * @return LLM响应
     */
    @ApiOperation("简单健康咨询")
    @GetMapping("/quick-advice")
    public ResponseEntity<LLMResponse> getQuickHealthAdvice(@RequestParam String question) {
        // 清除可能导致JSON解析问题的字符
        String cleanedQuestion = question
            .replace("\t", " ")
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("\"", "'");
            
        LLMRequest request = LLMRequest.builder()
                .systemPrompt("你是一个专业的医疗健康助手，提供简短有用的健康建议。注意：你不是医生，不能提供诊断和处方，严重问题建议就医。")
                .userPrompt(cleanedQuestion)
                .temperature(0.3f)
                .maxTokens(2000)
                .build();
        
        LLMResponse response = llmService.generateResponse(request);
        return ResponseEntity.ok(response);
    }
} 