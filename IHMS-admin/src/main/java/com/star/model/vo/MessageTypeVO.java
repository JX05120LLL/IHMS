package com.star.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageTypeVO {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 类型描述
     */
    private String detail;
}
