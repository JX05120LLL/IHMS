package com.star.service.impl;

import com.star.context.LocalThreadHolder;
import com.star.mapper.MessageMapper;
import com.star.mapper.UserMapper;
import com.star.common.CommonResult;
import com.star.common.CommonPageResult;
import com.star.common.Result;
import com.star.model.dto.query.extend.MessageQueryDto;
import com.star.model.dto.query.extend.UserQueryDto;
import com.star.em.IsReadEnum;
import com.star.em.MessageType;
import com.star.em.RoleEnum;
import com.star.model.entity.Message;
import com.star.model.entity.User;
import com.star.model.vo.MessageVO;
import com.star.service.IMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消息业务逻辑实现
 */
@Service
public class MessageServiceImpl implements IMessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 消息新增
     *
     * @param messages 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(List<Message> messages) {
        messageMapper.batchSave(messages);
        return CommonResult.success();
    }

    /**
     * 评论被别人回复了，需要给被回复者发送通知消息
     *
     * @param message 消息数据
     * @return Result<Void>
     */
    @Override
    public Result<Void> evaluationsReplySave(Message message) {
        message.setMessageType(MessageType.EVALUATIONS_BY_REPLY.getType());
        message.setContent("你的评论被回复了");
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setCreateTime(LocalDateTime.now());
        List<Message> entityListSave = new ArrayList<>();
        entityListSave.add(message);
        return save(entityListSave);
    }

    /**
     * 评论被点赞，需要给评论所属者发通知
     *
     * @param message 消息实体
     * @return Result<Void>
     */
    @Override
    public Result<Void> evaluationsUpvoteSave(Message message) {
        message.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        message.setContent("你的评论被点赞了");
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setCreateTime(LocalDateTime.now());
        List<Message> entityListSave = new ArrayList<>();
        entityListSave.add(message);
        return save(entityListSave);
    }

    /**
     * 系统通知 （一种是给全站用户发通知，另外一种是给指定用户发通知）
     *
     * @param messages 消息集合
     * @return Result<Void>
     */
    @Override
    public Result<Void> systemInfoSave(List<Message> messages) {
        return save(messages);
    }

    /**
     * 指标提醒 （由于用户一次性可以记录多个指标，所以我们这里也要传集合）
     *
     * @param messages 消息集合
     * @return Result<Void>
     */
    @Override
    public Result<Void> dataWordSave(List<Message> messages) {
        return save(messages);
    }

    /**
     * 消息删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        messageMapper.batchDelete(ids);
        return CommonResult.success();
    }

    /**
     * 消息查询
     *
     * @param messageQueryDto 查询参数
     * @return Result<List < MessageVO>>
     */
    @Override
    public Result<List<MessageVO>> query(MessageQueryDto messageQueryDto) {
        List<MessageVO> tagsList = messageMapper.query(messageQueryDto);
        Integer totalCount = messageMapper.queryCount(messageQueryDto);
        return CommonPageResult.success(tagsList, totalCount);
    }

    /**
     * 全站的系统通知
     *
     * @param message 消息通知
     * @return Result<Void> 通用响应体
     */
    @Override
    public Result<Void> systemInfoUsersSave(Message message) {
        // 这项数据是要向全站用户推送的消息体
        String messageContent = message.getContent();
        // 1. 先拿到全站用户的ID
        UserQueryDto userQueryDto = new UserQueryDto();
        List<User> userList = userMapper.query(userQueryDto);
        List<Message> messageList = new ArrayList<>();
        userList.forEach(user -> {
            // 管理员要不要推送？不用
            if (Objects.equals(RoleEnum.USER.getRole(), user.getUserRole())) {
                // 2. 组装消息
                // 他是普通用户，不是管理员，所以要推送
                Message saveEntity = new MessageVO();
                // 接收者用户ID
                saveEntity.setReceiverId(user.getId());
                // 消息体
                saveEntity.setContent(messageContent);
                // 标识为系统通知
                saveEntity.setMessageType(MessageType.SYSTEM_INFO.getType());
                // 初始状态：未读
                saveEntity.setIsRead(IsReadEnum.READ_NO.getStatus());
                // 发送时间
                saveEntity.setCreateTime(LocalDateTime.now());
                messageList.add(saveEntity);
            }
        });
        // 3. 去批量推送（新增）消息
        return save(messageList);
    }

    /**
     * 将全部消息设置为已读
     *
     * @return Result<Void> 响应
     */
    @Override
    public Result<Void> clearMessage() {
        // 这是当前操作用户的 ID
        Integer userId = LocalThreadHolder.getUserId();
        messageMapper.update(userId, IsReadEnum.READ_OK.getStatus());
        return CommonResult.success();
    }
}
