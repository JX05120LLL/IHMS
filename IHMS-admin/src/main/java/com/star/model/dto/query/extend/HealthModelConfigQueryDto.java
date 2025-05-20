package com.star.model.dto.query.extend;

import com.star.model.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HealthModelConfigQueryDto extends QueryDto {

    /**
     * 模型ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 全局变量
     */
    private Boolean isGlobal;

    /**
     * 配置名
     */
    private String name;
    /**
     * 配置简介
     */
    private String detail;
    /**
     * 模型配置单位
     */
    private String unit;
    /**
     * 模型配置符号
     */
    private String symbol;

}
