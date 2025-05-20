package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户画像实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_profile")
public class UserProfile {
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
     * 健康状态分类
     */
    private String healthStatus;
    
    /**
     * 健康风险等级(1-5)
     */
    private Byte riskLevel;
    
    /**
     * 活跃度(1-5)
     */
    private Byte activeLevel;
    
    /**
     * 偏好标签
     */
    private String preferenceTags;
    
    /**
     * 最后更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
} 