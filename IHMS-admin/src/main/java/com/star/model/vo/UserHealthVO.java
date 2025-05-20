package com.star.model.vo;

import com.star.model.entity.UserHealth;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserHealthVO extends UserHealth {

    /**
     * 用户名
     */
    private String userName;
    /**
     * 健康模型名称
     */
    private String name;
    /**
     * 健康模型单位
     */
    private String unit;
    /**
     * 健康模型符号
     */
    private String symbol;
    /**
     * 模型配置的正常值范围
     */
    private String valueRange;

}
