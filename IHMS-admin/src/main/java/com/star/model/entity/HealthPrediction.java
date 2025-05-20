package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康预测结果实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_prediction")
public class HealthPrediction {
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
     * 指标类型
     */
    private Integer metricType;
    
    /**
     * 预测时间点
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime predictionTime;
    
    /**
     * 预测值
     */
    private BigDecimal predictedValue;
    
    /**
     * 置信度(0-1)
     */
    private BigDecimal confidence;
    
    /**
     * 模型版本
     */
    private String modelVersion;
    
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationTime;
} 