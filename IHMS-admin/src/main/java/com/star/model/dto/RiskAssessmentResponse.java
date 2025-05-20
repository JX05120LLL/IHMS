package com.star.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康风险评估响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {

    /**
     * 评估ID
     */
    private Integer assessmentId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 风险等级(LOW/MEDIUM/HIGH)
     */
    private String riskLevel;
    
    /**
     * 总体风险级别(1-5)
     * 1: 极低风险
     * 2: 低风险
     * 3: 中等风险
     * 4: 高风险
     * 5: 极高风险
     */
    private Integer overallRiskLevel;

    /**
     * 风险因素列表
     */
    private List<String> riskFactors;

    /**
     * BMI指数
     */
    private Float bmiValue;

    /**
     * BMI评级(UNDERWEIGHT/NORMAL/OVERWEIGHT/OBESE)
     */
    private String bmiCategory;

    /**
     * 评估结果描述
     */
    private String assessmentResult;

    /**
     * 建议列表
     */
    private List<String> recommendations;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 预计下次评估时间
     */
    private LocalDateTime nextAssessmentTime;
}