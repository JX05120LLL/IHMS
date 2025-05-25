package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户健康记录实体
 */
@Data
public class UserHealth {
    /**
     * ID
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 健康模型ID
     */
    private Integer healthModelConfigId;
    /**
     * 用户记录的值
     */
    private String value;
    /**
     * 记录时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 测量时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measureTime;
    
    /**
     * 身高(cm)
     */
    private Double height;
    
    /**
     * 体重(kg)
     */
    private Double weight;
    
    /**
     * BMI指数
     */
    private Double bmi;
    
    /**
     * 收缩压(mmHg)
     */
    private Integer systolicPressure;
    
    /**
     * 舒张压(mmHg)
     */
    private Integer diastolicPressure;
    
    /**
     * 心率(bpm)
     */
    private Integer heartRate;
    
    /**
     * 血糖(mmol/L)
     */
    private Double bloodSugar;
    
    /**
     * 血氧饱和度(%)
     */
    private Integer bloodOxygen;
}
