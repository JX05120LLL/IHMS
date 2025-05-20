package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
public class Message {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息类型
     */
    private Integer messageType;
    /**
     * 接收者用户ID
     */
    private Integer receiverId;
    /**
     * 发送者用户ID
     */
    private Integer senderId;
    /**
     * 消息是否已经阅读  新增时设置为 0：false ，1：true
     */
    private Boolean isRead;
    /**
     * 内容ID  如果消息类型是属于互动相关的数据（评论被别人评论了、你的评论被别人点赞了）
     */
    private Integer contentId;
    /**
     * 消息发送时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
