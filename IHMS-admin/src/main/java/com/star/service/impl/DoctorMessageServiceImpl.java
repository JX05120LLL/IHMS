package com.star.service.impl;

import com.star.mapper.DoctorConsultationMapper;
import com.star.mapper.DoctorMessageMapper;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.entity.DoctorConsultation;
import com.star.model.entity.DoctorMessage;
import com.star.service.IDoctorMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 医患消息服务实现类
 */
@Service
public class DoctorMessageServiceImpl extends ServiceImpl<DoctorMessageMapper, DoctorMessage> implements IDoctorMessageService {

    @Resource
    private DoctorConsultationMapper consultationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> sendMessage(DoctorMessage message) {
        // 检查会话是否存在和有效
        DoctorConsultation consultation = consultationMapper.selectById(message.getConsultationId());
        if (consultation == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "会话不存在");
        }
        if (consultation.getStatus() == 1) {
            return Result.failure(ResultCodeEnum.BUSINESS_ERROR, "会话已结束，无法发送消息");
        }
        
        // 创建一个新的消息对象，而不是直接修改传入的对象
        DoctorMessage newMessage = new DoctorMessage();
        newMessage.setSenderId(message.getSenderId());
        newMessage.setReceiverId(message.getReceiverId());
        newMessage.setConsultationId(message.getConsultationId());
        newMessage.setContent(message.getContent());
        newMessage.setMessageType(message.getMessageType());
        newMessage.setHealthDataId(message.getHealthDataId());
        newMessage.setCreateTime(new Date());
        newMessage.setIsRead(0); // 0:未读
        
        // 保存消息
        boolean save = this.save(newMessage);
        if (!save) {
            return Result.failure(ResultCodeEnum.BUSINESS_ERROR, "发送消息失败");
        }
        
        // 更新会话最后更新时间
        consultation.setUpdateTime(new Date());
        consultationMapper.updateById(consultation);
        
        return Result.success(newMessage.getId());
    }

    @Override
    public Result<List<DoctorMessage>> getMessageList(Integer consultationId) {
        // 检查会话是否存在
        DoctorConsultation consultation = consultationMapper.selectById(consultationId);
        if (consultation == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "会话不存在");
        }
        
        // 查询消息列表
        List<DoctorMessage> list = this.list(new LambdaQueryWrapper<DoctorMessage>()
                .eq(DoctorMessage::getConsultationId, consultationId)
                .orderByAsc(DoctorMessage::getCreateTime));
        
        return Result.success(list);
    }

    @Override
    public Result<Boolean> markMessageAsRead(Integer id) {
        DoctorMessage message = this.getById(id);
        if (message == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "消息不存在");
        }
        
        message.setIsRead(1); // 1:已读
        boolean update = this.updateById(message);
        
        return Result.success(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> shareHealthData(DoctorMessage message) {
        // 验证消息类型
        if (message.getMessageType() != 2) {
            message.setMessageType(2); // 2:健康数据
        }
        
        // 验证健康数据ID
        if (message.getHealthDataId() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "请选择要分享的健康数据");
        }
        
        // 验证发送者ID是否设置
        if (message.getSenderId() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "发送者ID不能为空");
        }
        
        // 验证会话ID是否设置
        if (message.getConsultationId() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "会话ID不能为空");
        }
        
        // 验证接收者ID是否设置
        if (message.getReceiverId() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "接收者ID不能为空");
        }
        
        // 复用sendMessage方法
        return this.sendMessage(message);
    }

    @Override
    public Result<Integer> getUnreadCount(Integer userId) {
        // 查询未读消息数量
        long count = this.count(new LambdaQueryWrapper<DoctorMessage>()
                .eq(DoctorMessage::getReceiverId, userId)
                .eq(DoctorMessage::getIsRead, 0));
        
        // 安全转换为Integer
        return Result.success(count > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) count);
    }
} 