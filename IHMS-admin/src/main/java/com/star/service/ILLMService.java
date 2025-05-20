package com.star.service;

import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大模型服务接口
 */
@Service
public interface ILLMService {
    
    /**
     * 生成文本响应
     * @param request 请求对象
     * @return 响应对象
     */
    LLMResponse generateResponse(LLMRequest request);
    
    /**
     * 生成聊天响应
     * @param request 请求对象
     * @return 响应对象
     */
    LLMResponse generateChatResponse(LLMRequest request);
    
    /**
     * 批量处理请求（节省API调用）
     * @param requests 请求列表
     * @return 响应列表
     */
    List<LLMResponse> batchProcess(List<LLMRequest> requests);
} 