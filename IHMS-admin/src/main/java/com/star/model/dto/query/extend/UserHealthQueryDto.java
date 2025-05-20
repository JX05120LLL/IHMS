package com.star.model.dto.query.extend;

import com.star.model.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserHealthQueryDto extends QueryDto {

    /**
     * 健康记录ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 健康模型ID
     */
    private Integer healthModelConfigId;

}
