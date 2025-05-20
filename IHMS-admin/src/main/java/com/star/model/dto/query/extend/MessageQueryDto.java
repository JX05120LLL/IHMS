package com.star.model.dto.query.extend;

import com.star.model.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageQueryDto extends QueryDto {

    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 发送者ID
     */
    private Integer senderId;
    /**
     * 内容ID
     */
    private Integer contentId;
    /**
     * 消息类型
     */
    private Integer messageType;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否阅读
     */
    private Boolean isRead;

}
