package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 建议反馈实体
 */
@Data
public class AdviceFeedback {
    /**
     * ID
     */
    private Integer id;
    
    /**
     * 健康建议ID
     */
    private Integer adviceId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 评分(1-5)
     */
    private Byte rating;
    
    /**
     * 反馈内容
     */
    private String feedback;
    
    /**
     * 反馈时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime feedbackTime;
} 