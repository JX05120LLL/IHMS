package com.star.model.dto.query.extend;

import com.star.model.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewsSaveQueryDto extends QueryDto {

    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 健康资讯ID
     */
    private Integer newsId;

}
