package com.star.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "大模型请求参数")
public class LLMRequest {
    /**
     * 系统提示词
     */
    @ApiModelProperty(value = "系统提示词", example = "你是一个健康顾问AI助手")
    private String systemPrompt;
    
    /**
     * 用户提示词
     */
    @ApiModelProperty(value = "用户提示词", required = true, example = "什么是高血压，如何预防？")
    private String userPrompt;
    
    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称", example = "deepseek-chat")
    private String modelName;
    
    /**
     * 温度参数(0-1)，控制随机性
     */
    @ApiModelProperty(value = "温度参数(0-1)，控制随机性，值越大越随机", example = "0.7")
    private Float temperature;
    
    /**
     * 最大令牌数
     */
    @ApiModelProperty(value = "最大令牌数", example = "2000")
    private Integer maxTokens;
} 