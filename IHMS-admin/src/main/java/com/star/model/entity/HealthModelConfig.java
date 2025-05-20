package com.star.model.entity;

import lombok.Data;

/**
 * 健康模型实体类
 */
@Data
public class HealthModelConfig {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 配置名
     */
    private String name;
    /**
     * 配置简介
     */
    private String detail;
    /**
     * 图标
     */
    private String cover;
    /**
     * 单位
     */
    private String unit;
    /**
     * 符号
     */
    private String symbol;
    /**
     * 正常值范围   10,20 (10,20)
     */
    private String valueRange;
    /**
     * 是否是全局模型
     */
    private Boolean isGlobal;
}
