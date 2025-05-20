package com.star.controller;

import com.star.annotation.Protector;
import com.star.context.UserContext;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.entity.DoctorMessage;
import com.star.service.IDoctorMessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 医患消息控制器
 */
@RestController
@RequestMapping("/doctor-message")
public class DoctorMessageController {

    @Resource
    private IDoctorMessageService messageService;

    /**
     * 发送消息
     * 
     * @param message 消息信息
     * @return 消息ID
     */
    @Protector
    @PostMapping("/send")
    @ResponseBody
    public Result<Integer> sendMessage(@RequestBody DoctorMessage message) {
        // 设置发送者为当前用户
        if (UserContext.getUser() == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "用户未登录或会话已过期");
        }
        message.setSenderId(UserContext.getUser().getId());
        return messageService.sendMessage(message);
    }

    /**
     * 获取消息列表
     * 
     * @param consultationId 会话ID
     * @return 消息列表
     */
    @Protector
    @GetMapping("/list")
    @ResponseBody
    public Result<List<DoctorMessage>> getMessageList(@RequestParam Integer consultationId) {
        return messageService.getMessageList(consultationId);
    }

    /**
     * 标记消息已读
     * 
     * @param id 消息ID
     * @return 标记结果
     */
    @Protector
    @PutMapping("/read/{id}")
    @ResponseBody
    public Result<Boolean> markMessageAsRead(@PathVariable Integer id) {
        return messageService.markMessageAsRead(id);
    }

    /**
     * 分享健康数据
     * 
     * @param message 消息信息
     * @return 消息ID
     */
    @Protector
    @PostMapping("/share")
    @ResponseBody
    public Result<Integer> shareHealthData(@RequestBody DoctorMessage message) {
        // 设置发送者为当前用户
        if (UserContext.getUser() == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "用户未登录或会话已过期");
        }
        message.setSenderId(UserContext.getUser().getId());
        return messageService.shareHealthData(message);
    }
    
    /**
     * 获取未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    @Protector
    @GetMapping("/unread/count")
    @ResponseBody
    public Result<Integer> getUnreadCount(@RequestParam(required = false) Integer userId) {
        // 优先从上下文获取用户ID，如果为null则使用请求参数中的userId
        Integer currentUserId = null;
        if (UserContext.getUser() != null) {
            currentUserId = UserContext.getUser().getId();
        } else {
            currentUserId = userId;
        }
        
        if (currentUserId == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "未能获取用户ID");
        }
        
        return messageService.getUnreadCount(currentUserId);
    }
} 