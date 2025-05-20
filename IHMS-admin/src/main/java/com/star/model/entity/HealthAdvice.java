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
 * 健康建议实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_advice")
public class HealthAdvice {
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
     * 建议类型
     */
    private String adviceType;
    
    /**
     * 建议内容
     */
    private String content;
    
    /**
     * 来源(AI/专家)
     */
    private String source;
    
    /**
     * 优先级(1-5)
     */
    private Byte priority;
    
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 是否已读(0:否,1:是)
     */
    private Byte isRead;
    
    /**
     * 是否已执行(0:否,1:是)
     */
    private Byte isImplemented;
} 