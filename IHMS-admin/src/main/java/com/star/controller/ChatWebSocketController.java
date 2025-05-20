package com.star.controller;

import com.star.mapper.DoctorRepository;
import com.star.mapper.UserRepository;
import com.star.model.dto.ChatMessage;
import com.star.model.entity.Doctor;
import com.star.model.entity.DoctorMessage;
import com.star.model.entity.User;
import com.star.service.IDoctorMessageService;
import com.star.service.IUserHealthService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Date;

/**
 * WebSocket聊天控制器
 * 处理医生和患者之间的实时消息
 */
@Controller
public class ChatWebSocketController {
    
    @Resource
    private SimpMessagingTemplate messagingTemplate;
    
    @Resource
    private IDoctorMessageService doctorMessageService;
    
    @Resource
    private IUserHealthService userHealthService;
    
    @Resource
    private UserRepository userRepository;
    
    @Resource
    private DoctorRepository doctorRepository;

    /**
     * 处理发送消息请求
     * 
     * @param message 消息内容
     * @param consultationId 会话ID
     */
    @MessageMapping("/chat/{consultationId}")
    public void sendMessage(ChatMessage message, @DestinationVariable Integer consultationId) {
        // 设置会话ID
        message.setConsultationId(consultationId);
        
        // 设置发送时间
        message.setCreateTime(new Date());
        
        // 填充发送者信息
        if (message.getSenderType() == 0) { // 患者
            User user = userRepository.selectById(message.getSenderId());
            if (user != null) {
                message.setSenderName(user.getUserName());
            }
        } else { // 医生
            Doctor doctor = doctorRepository.selectById(message.getSenderId());
            if (doctor != null) {
                message.setSenderName(doctor.getName());
            }
        }
        
        // 保存消息到数据库
        DoctorMessage doctorMessage = new DoctorMessage();
        doctorMessage.setConsultationId(consultationId);
        doctorMessage.setSenderId(message.getSenderId());
        doctorMessage.setReceiverId(message.getReceiverId());
        doctorMessage.setContent(message.getContent());
        doctorMessage.setMessageType(message.getMessageType());
        doctorMessage.setHealthDataId(message.getHealthDataId());
        doctorMessage.setIsRead(0); // 0:未读
        doctorMessage.setCreateTime(message.getCreateTime());
        
        // 保存消息，获取消息ID
        doctorMessageService.sendMessage(doctorMessage);
        message.setId(doctorMessage.getId());
        
        // 如果是健康数据消息，加载健康数据
        if (message.getMessageType() == 2 && message.getHealthDataId() != null) {
            try {
                // 加载健康数据
                message.setHealthData(userHealthService.getHealthRecordById(message.getHealthDataId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 发送消息到指定的主题
        messagingTemplate.convertAndSend("/topic/consultation/" + consultationId, message);
    }
} 