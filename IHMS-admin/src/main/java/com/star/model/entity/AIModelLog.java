package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI模型调用日志实体
 */
@Data
public class AIModelLog {
    /**
     * ID
     */
    private Integer id;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * API端点
     */
    private String apiEndpoint;
    
    /**
     * 请求时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;
    
    /**
     * 响应时间(ms)
     */
    private Integer responseTime;
    
    /**
     * 状态(0:失败,1:成功)
     */
    private Byte status;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 