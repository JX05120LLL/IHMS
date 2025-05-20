package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 医患聊天消息实体类
 */
@Data
@TableName("doctor_message")
public class DoctorMessage {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
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
     * 是否已读(0:未读,1:已读)
     */
    private Integer isRead;
    
    /**
     * 发送时间
     */
    private Date createTime;
} 