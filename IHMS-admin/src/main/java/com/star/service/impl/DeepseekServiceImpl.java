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
                // 构建请求体 - 修改为符合DeepSeek API的格式
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", StringUtils.hasText(request.getModelName()) ? request.getModelName() : model);
                
                List<Map<String, String>> messages = new ArrayList<>();
                
                // 添加系统提示词
                if (StringUtils.hasText(request.getSystemPrompt())) {
                    Map<String, String> systemMessage = new HashMap<>();
                    systemMessage.put("role", "system");
                    
                    // 清理系统提示词内容，移除换行符和特殊字符
                    String cleanSystemPrompt = request.getSystemPrompt()
                        .replace("\n", " ")
                        .replace("\r", " ")
                        .replace("\t", " ")
                        .replace("\"", "'");
                    
                    systemMessage.put("content", cleanSystemPrompt);
                    messages.add(systemMessage);
                }
                
                // 添加用户提示词，确保内容格式正确
                Map<String, String> userMessage = new HashMap<>();
                userMessage.put("role", "user");
                
                // 清理用户提示词内容
                String cleanUserPrompt = request.getUserPrompt() != null ? 
                    request.getUserPrompt()
                        .replace("\n", " ")
                        .replace("\r", " ")
                        .replace("\t", " ")
                        .replace("\"", "'") :
                    "你好，我需要些健康建议。";
                
                userMessage.put("content", cleanUserPrompt);
                messages.add(userMessage);
                
                requestBody.put("messages", messages);
                
                // 设置温度参数
                if (request.getTemperature() != null) {
                    requestBody.put("temperature", request.getTemperature());
                } else {
                    requestBody.put("temperature", 0.3);
                }
                
                // 设置最大令牌数
                if (request.getMaxTokens() != null) {
                    requestBody.put("max_tokens", request.getMaxTokens());
                } else {
                    requestBody.put("max_tokens", 2000);
                }
                
                // 添加stream参数，设为false以获取完整响应
                requestBody.put("stream", false);
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + apiKey);
                
                // 如果不是第一次尝试，记录重试信息
                if (retryCount > 0) {
                    log.info("正在进行第{}次重试Deepseek API调用", retryCount);
                }
                
                log.info("DeepSeek API请求: {}", requestBody);
                
                // 发送请求
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                log.info("正在发送请求到: {}", apiUrl);
                
                ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, Map.class);
                
                // 记录完整响应
                log.info("DeepSeek API响应状态: {}", response.getStatusCode());
                
                // 解析响应
                Map responseBody = response.getBody();
                log.info("DeepSeek API响应内容: {}", responseBody);
                
                if (responseBody != null && responseBody.containsKey("choices")) {
                    List<Map> choices = (List<Map>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map choice = choices.get(0);
                        Map message = (Map) choice.get("message");
                        String content = (String) message.get("content");
                        
                        log.info("DeepSeek API返回内容: {}", content);
                        
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
                
                // 返回一个默认回复，而不是错误信息
                return createDefaultResponse(request);
                
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
                log.error("Deepseek API调用失败，无法重试: {}", e.getMessage(), e);
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
        
        // 返回一个默认回复，而不是错误信息
        return createDefaultResponse(request);
    }
    
    /**
     * 创建默认响应，当API调用失败时使用
     */
    private LLMResponse createDefaultResponse(LLMRequest request) {
        String userPrompt = request.getUserPrompt();
        String defaultResponse;
        
        if (userPrompt != null && userPrompt.contains("头疼")) {
            defaultResponse = "头痛可能由多种原因引起，如压力、疲劳、脱水或者感冒等。建议您可以：\n\n1. 充分休息，确保睡眠充足\n2. 保持良好的水分摄入\n3. 避免过度用眼和长时间盯着屏幕\n4. 如有必要，可以服用非处方止痛药如对乙酰氨基酚\n\n如果头痛剧烈、持续时间长或伴随其他症状，建议及时就医。";
        } else if (userPrompt != null && userPrompt.contains("血压")) {
            defaultResponse = "关于血压，建议保持健康生活方式：\n\n1. 控制盐分摄入\n2. 规律运动，每周至少150分钟中等强度运动\n3. 保持健康体重\n4. 限制酒精摄入\n5. 戒烟\n6. 管理压力\n\n正常血压应低于120/80 mmHg。如果您的血压持续偏高，建议咨询医生。";
        } else {
            defaultResponse = "您好，我是IHMS医疗顾问。很抱歉，我暂时无法连接到后端服务。以下是一些通用健康建议：\n\n1. 保持充足睡眠，成年人每晚应睡7-9小时\n2. 均衡饮食，多摄入蔬果、全谷物和优质蛋白\n3. 规律运动，每周至少150分钟中等强度活动\n4. 保持良好心态，学会应对压力\n5. 定期体检，关注健康指标变化\n\n如有具体健康问题，建议咨询专业医生。";
        }
        
        return LLMResponse.builder()
                .content(defaultResponse)
                .success(true)
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