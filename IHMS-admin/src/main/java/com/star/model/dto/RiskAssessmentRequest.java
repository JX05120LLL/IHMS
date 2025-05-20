package com.star.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 健康风险评估请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentRequest {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 血压-收缩压
     */
    private Integer systolicPressure;

    /**
     * 血压-舒张压
     */
    private Integer diastolicPressure;

    /**
     * 心率
     */
    private Integer heartRate;

    /**
     * 血糖
     */
    private Float bloodSugar;

    /**
     * 体重(kg)
     */
    private Float weight;

    /**
     * 身高(cm)
     */
    private Float height;

    /**
     * 运动时间(分钟/天)
     */
    private Integer exerciseMinutes;

    /**
     * 睡眠时间(小时)
     */
    private Float sleepHours;

    /**
     * 营养状况(POOR/NORMAL/GOOD/EXCELLENT)
     */
    private String nutritionQuality;

    /**
     * 吸烟状况(YES/NO/OCCASIONAL)
     */
    private String smokingStatus;

    /**
     * 饮酒状况(YES/NO/OCCASIONAL)
     */
    private String alcoholStatus;
}