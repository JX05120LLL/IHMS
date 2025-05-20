package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.MessageQueryDto;
import com.star.model.entity.Message;
import com.star.model.vo.MessageVO;

import java.util.List;

/**
 * 消息业务逻辑接口
 */
public interface IMessageService {

    Result<Void> save(List<Message> messages);

    Result<Void> evaluationsReplySave(Message message);

    Result<Void> evaluationsUpvoteSave(Message message);

    Result<Void> systemInfoSave(List<Message> messages);

    Result<Void> dataWordSave(List<Message> messages);

    Result<Void> batchDelete(List<Long> ids);

    Result<List<MessageVO>> query(MessageQueryDto messageQueryDto);

    Result<Void> systemInfoUsersSave(Message message);

    Result<Void> clearMessage();


}
