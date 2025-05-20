package com.star.service;

import com.star.common.Result;
import com.star.model.entity.DoctorMessage;

import java.util.List;

/**
 * 医患消息服务接口
 */
public interface IDoctorMessageService {

    /**
     * 发送消息
     * 
     * @param message 消息信息
     * @return 消息ID
     */
    Result<Integer> sendMessage(DoctorMessage message);
    
    /**
     * 获取消息列表
     * 
     * @param consultationId 会话ID
     * @return 消息列表
     */
    Result<List<DoctorMessage>> getMessageList(Integer consultationId);
    
    /**
     * 标记消息已读
     * 
     * @param id 消息ID
     * @return 标记结果
     */
    Result<Boolean> markMessageAsRead(Integer id);
    
    /**
     * 分享健康数据
     * 
     * @param message 消息信息
     * @return 消息ID
     */
    Result<Integer> shareHealthData(DoctorMessage message);
    
    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Result<Integer> getUnreadCount(Integer userId);
} 