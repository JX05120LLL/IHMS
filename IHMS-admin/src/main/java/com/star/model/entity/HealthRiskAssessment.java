package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康风险评估实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_risk_assessment")
public class HealthRiskAssessment {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 评估类型
     */
    private String assessmentType;
    
    /**
     * 风险等级(1-5)
     */
    private Byte riskLevel;
    
    /**
     * 风险因素(JSON格式)
     */
    private String riskFactors;
    
    /**
     * 评估结果(JSON格式)
     */
    private String assessmentResult;
    
    /**
     * 评估时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assessmentTime;
} 