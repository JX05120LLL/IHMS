package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康知识实体
 */
@Data
public class HealthKnowledge {
    /**
     * ID
     */
    private Integer id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 关键词
     */
    private String keywords;
    
    /**
     * 来源
     */
    private String source;
    
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 