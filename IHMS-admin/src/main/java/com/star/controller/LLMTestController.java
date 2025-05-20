package com.star.controller;

import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.service.ILLMService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 大模型调用测试控制器
 */
@Api(tags = "大模型API测试接口")
@RestController
@RequestMapping("/api/llm/test")
public class LLMTestController {

    @Autowired
    @Qualifier("deepseekService")  // 确保使用deepseek服务
    private ILLMService llmService;

    /**
     * 测试大模型文本生成
     */
    @ApiOperation("测试大模型完整文本生成")
    @PostMapping("/generate")
    public ResponseEntity<LLMResponse> testGenerate(@RequestBody LLMRequest request) {
        LLMResponse response = llmService.generateResponse(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 简单测试接口
     */
    @ApiOperation("简单测试大模型调用")
    @GetMapping("/hello")
    public ResponseEntity<LLMResponse> testHello(
            @ApiParam(value = "提示词", defaultValue = "写一首关于健康的短诗") 
            @RequestParam(defaultValue = "写一首关于健康的短诗") String prompt) {
        
        LLMRequest request = LLMRequest.builder()
                .systemPrompt("你是一个健康顾问AI助手，擅长提供简洁有用的健康建议。")
                .userPrompt(prompt)
                .temperature(0.7f)
                .build();
        
        LLMResponse response = llmService.generateResponse(request);
        return ResponseEntity.ok(response);
    }
} 