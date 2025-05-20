package com.star.service.impl;

import com.star.mapper.AIModelLogMapper;
import com.star.model.dto.LLMRequest;
import com.star.model.dto.LLMResponse;
import com.star.model.entity.AIModelLog;
import com.star.service.ILLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Deepseek大模型服务实现
 */
@Slf4j
@Service("deepseekService")
@ConditionalOnProperty(name = "llm.provider", havingValue = "deepseek")
public class DeepseekServiceImpl implements ILLMService {
    
    @Value("${deepseek.api-key}")
    private String apiKey;
    
    @Value("${deepseek.model:deepseek-chat}")
    private String model;
    
    @Value("${deepseek.api-url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${deepseek.max-retries:2}")
    private int maxRetries;
    
    @Autowired
    @Qualifier("llmRestTemplate")
    private RestTemplate restTemplate;
    
    @Autowired
    private AIModelLogMapper aiModelLogMapper;
    
    /**
     * 生成文本响应
     */
    @Override
    public LLMResponse generateResponse(LLMRequest request) {
        long startTime = System.currentTimeMillis();
        AIModelLog logRecord = createLogRecord("Deepseek", apiUrl);
        
        // 重试机制
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount <= maxRetries) {
            try {
                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", StringUtils.hasText(request.getModelName()) ? request.getModelName() : model);
                
                List<Map<String, String>> messages = new ArrayList<>();
                
                // 添加系统提示词
                if (StringUtils.hasText(request.getSystemPrompt())) {
                    Map<String, String> systemMessage = new HashMap<>();
                    systemMessage.put("role", "system");
                    systemMessage.put("content", request.getSystemPrompt());
                    messages.add(systemMessage);
                }
                
                // 添加用户提示词
                Map<String, String> userMessage = new HashMap<>();
                userMessage.put("role", "user");
                userMessage.put("content", request.getUserPrompt());
                messages.add(userMessage);
                
                requestBody.put("messages", messages);
                
                // 设置温度参数
                if (request.getTemperature() != null) {
                    requestBody.put("temperature", request.getTemperature());
                }
                
                // 设置最大令牌数
                if (request.getMaxTokens() != null) {
                    requestBody.put("max_tokens", request.getMaxTokens());
                }
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + apiKey);
                
                // 如果不是第一次尝试，记录重试信息
                if (retryCount > 0) {
                    log.info("正在进行第{}次重试Deepseek API调用", retryCount);
                }
                
                // 发送请求
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, Map.class);
                
                // 解析响应
                Map responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("choices")) {
                    List<Map> choices = (List<Map>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map choice = choices.get(0);
                        Map message = (Map) choice.get("message");
                        String content = (String) message.get("content");
                        
                        // 记录成功日志
                        logRecord.setStatus((byte) 1);
                        logRecord.setResponseTime((int)(System.currentTimeMillis() - startTime));
                        try {
                            saveLog(logRecord);
                        } catch (Exception e) {
                            log.error("保存成功日志失败，但不影响正常返回", e);
                        }
                        
                        return LLMResponse.builder()
                                .content(content)
                                .usedTokens(responseBody.containsKey("usage") ? 
                                        (Integer)((Map)responseBody.get("usage")).get("total_tokens") : null)
                                .requestId(responseBody.containsKey("id") ? (String)responseBody.get("id") : null)
                                .success(true)
                                .build();
                    }
                }
                
                // 如果走到这里，说明API返回了但解析失败
                String errorMsg = "Failed to parse Deepseek response: " + responseBody;
                log.warn(errorMsg);
                
                // 记录失败日志
                logRecord.setStatus((byte) 0);
                logRecord.setErrorMessage(errorMsg);
                logRecord.setResponseTime((int)(System.currentTimeMillis() - startTime));
                try {
                    saveLog(logRecord);
                } catch (Exception e) {
                    log.error("保存失败日志失败", e);
                }
                
                return LLMResponse.builder()
                        .success(false)
                        .errorMessage(errorMsg)
                        .build();
                
            } catch (ResourceAccessException e) {
                // 网络错误，可以重试
                lastException = e;
                log.warn("Deepseek API调用超时或网络错误，将尝试重试 ({}/{}): {}", retryCount+1, maxRetries+1, e.getMessage());
                retryCount++;
                
                // 等待一小段时间再重试
                try {
                    Thread.sleep(1000 * retryCount); // 递增等待时间
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                // 其他错误，直接退出
                lastException = e;
                log.error("Deepseek API调用失败，无法重试", e);
                break;
            }
        }
        
        // 所有重试都失败了
        log.error("Deepseek API在{}次尝试后仍然调用失败", maxRetries+1);
        
        // 记录失败日志
        logRecord.setStatus((byte) 0);
        logRecord.setErrorMessage(lastException != null ? lastException.getMessage() : "Unknown error");
        logRecord.setResponseTime((int)(System.currentTimeMillis() - startTime));
        try {
            saveLog(logRecord);
        } catch (Exception e) {
            log.error("保存失败日志失败", e);
        }
        
        return LLMResponse.builder()
                .success(false)
                .errorMessage("Deepseek API调用失败: " + (lastException != null ? lastException.getMessage() : "Unknown error"))
                .build();
    }
    
    /**
     * 生成对话响应
     */
    @Override
    public LLMResponse generateChatResponse(LLMRequest request) {
        // 聊天模式实现与普通模式类似
        return generateResponse(request);
    }
    
    /**
     * 批量处理请求
     */
    @Override
    public List<LLMResponse> batchProcess(List<LLMRequest> requests) {
        return requests.stream()
                .map(this::generateResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建日志记录
     */
    private AIModelLog createLogRecord(String modelName, String apiEndpoint) {
        AIModelLog log = new AIModelLog();
        log.setModelName(modelName);
        log.setApiEndpoint(apiEndpoint);
        log.setRequestTime(LocalDateTime.now());
        return log;
    }
    
    /**
     * 保存日志记录
     */
    private void saveLog(AIModelLog logEntity) {
        try {
            aiModelLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.error("保存AI模型调用日志失败", e);
        }
    }
} 