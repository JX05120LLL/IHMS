package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康异常记录实体
 */
@Data
public class HealthAnomaly {
    /**
     * ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 关联健康记录ID
     */
    private Integer recordId;
    
    /**
     * 异常类型
     */
    private String anomalyType;
    
    /**
     * 异常值
     */
    private BigDecimal anomalyValue;
    
    /**
     * 预期范围
     */
    private String expectedRange;
    
    /**
     * 严重程度(1-5)
     */
    private Byte severity;
    
    /**
     * 检测时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime detectionTime;
    
    /**
     * 是否确认(0:否,1:是)
     */
    private Byte isConfirmed;
} 