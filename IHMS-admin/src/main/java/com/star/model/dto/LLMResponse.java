package com.star.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "大模型响应结果")
public class LLMResponse {
    /**
     * 响应内容
     */
    @ApiModelProperty(value = "响应内容", example = "高血压是指血压持续升高超过正常范围...")
    private String content;
    
    /**
     * 使用的令牌数
     */
    @ApiModelProperty(value = "使用的令牌数", example = "526")
    private Integer usedTokens;
    
    /**
     * 请求ID
     */
    @ApiModelProperty(value = "请求ID", example = "req_123456789")
    private String requestId;
    
    /**
     * 错误信息
     */
    @ApiModelProperty(value = "错误信息", example = "API调用失败: 连接超时")
    private String errorMessage;
    
    /**
     * 是否成功
     */
    @ApiModelProperty(value = "是否成功", example = "true")
    private Boolean success;
} 