package com.star.service.ai;


import com.star.model.dto.RiskAssessmentRequest;
import com.star.model.dto.RiskAssessmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 健康风险评估服务
 * 整合外部AI接口进行健康风险评估
 */
@Service
@Slf4j
public class HealthRiskAssessmentService {
    
    @Value("${ai.service.risk-assessment.url:https://api.challenge-cup.ai/health/risk-assessment}")
    private String serviceUrl;
    
    @Value("${ai.service.risk-assessment.api-key:default-api-key}")
    private String apiKey;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 调用外部健康风险评估服务
     * @param request 风险评估请求
     * @return 风险评估结果
     */
    public RiskAssessmentResponse assessHealthRisk(RiskAssessmentRequest request) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        // 发起请求
        HttpEntity<RiskAssessmentRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            log.info("正在调用健康风险评估服务: {}", serviceUrl);
            ResponseEntity<RiskAssessmentResponse> response = 
                restTemplate.exchange(serviceUrl, HttpMethod.POST, entity, RiskAssessmentResponse.class);
            
            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("健康风险评估服务调用成功");
                return response.getBody();
            } else {
                // 接口调用失败，降级为本地评估
                log.warn("风险评估接口调用失败，状态码: " + response.getStatusCodeValue());
                return fallbackToLocalAssessment(request);
            }
        } catch (Exception e) {
            log.error("风险评估接口调用异常", e);
            // 接口调用异常，降级为本地评估
            return fallbackToLocalAssessment(request);
        }
    }
    
    /**
     * 降级策略：本地简单风险评估
     * @param request 原始请求
     * @return 基于本地规则的风险评估
     */
    private RiskAssessmentResponse fallbackToLocalAssessment(RiskAssessmentRequest request) {
        log.info("使用本地降级策略进行风险评估");
        
        // 创建降级响应
        RiskAssessmentResponse response = new RiskAssessmentResponse();
        
        // 设置默认风险等级（中等风险）
        response.setOverallRiskLevel(3);
        
        // 根据本地规则生成简单建议
        response.setRecommendations(generateBasicRecommendations(request));
        
        return response;
    }
    
    /**
     * 生成基本健康建议
     * @param request 原始请求
     * @return 基本健康建议列表
     */
    private java.util.List<String> generateBasicRecommendations(RiskAssessmentRequest request) {
        java.util.List<String> recommendations = new java.util.ArrayList<>();
        
        // 添加通用健康建议
        recommendations.add("保持规律作息，确保充足睡眠");
        recommendations.add("每天保持适量运动，至少30分钟");
        recommendations.add("保持均衡饮食，多吃蔬果");
        recommendations.add("定期体检，关注健康指标变化");
        
        return recommendations;
    }
} 