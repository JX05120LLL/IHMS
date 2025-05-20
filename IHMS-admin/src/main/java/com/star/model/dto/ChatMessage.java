package com.star.model.dto;

import lombok.Data;
import java.util.Date;

/**
 * WebSocket聊天消息DTO
 * 用于前后端传输消息
 */
@Data
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private Integer id;
    
    /**
     * 关联会话ID
     */
    private Integer consultationId;
    
    /**
     * 发送者ID
     */
    private Integer senderId;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 发送者类型(0:患者,1:医生)
     */
    private Integer senderType;
    
    /**
     * 接收者ID
     */
    private Integer receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型(0:文本,1:图片,2:健康数据)
     */
    private Integer messageType;
    
    /**
     * 关联健康数据ID
     */
    private Integer healthDataId;
    
    /**
     * 健康数据对象(当messageType=2时使用)
     */
    private Object healthData;
    
    /**
     * 发送时间
     */
    private Date createTime;
} 