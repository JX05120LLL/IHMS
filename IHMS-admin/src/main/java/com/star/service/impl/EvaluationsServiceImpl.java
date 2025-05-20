package com.star.service.impl;

import com.star.context.LocalThreadHolder;
import com.star.mapper.EvaluationsMapper;
import com.star.mapper.MessageMapper;
import com.star.mapper.UserMapper;
import com.star.common.CommonResult;
import com.star.common.CommonPageResult;
import com.star.common.Result;
import com.star.model.dto.query.extend.EvaluationsQueryDto;
import com.star.model.dto.query.extend.MessageQueryDto;
import com.star.em.IsReadEnum;
import com.star.em.MessageType;
import com.star.model.entity.Evaluations;
import com.star.model.entity.Message;
import com.star.model.entity.User;
import com.star.model.vo.CommentChildVO;
import com.star.model.vo.CommentParentVO;
import com.star.model.vo.EvaluationsVO;
import com.star.service.IEvaluationsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现类
 */
@Service
public class EvaluationsServiceImpl implements IEvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageMapper messageMapper;

    /**
     * 评论保存
     *
     * @param evaluations 评论数据
     */
    private void saveEvaluations(Evaluations evaluations) {
        evaluations.setCommenterId(LocalThreadHolder.getUserId());
        evaluations.setCreateTime(LocalDateTime.now());
        evaluationsMapper.save(evaluations);
    }

    /**
     * 查询评论数据
     *
     * @param parentId 父级ID
     * @return List<CommentChildVO>
     */
    private List<CommentChildVO> getCommentChild(Integer parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        EvaluationsQueryDto evaluationsQueryDto = new EvaluationsQueryDto();
        evaluationsQueryDto.setId(parentId);
        return evaluationsMapper.query(evaluationsQueryDto);
    }

    /**
     * 评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> insert(Evaluations evaluations) {
        User userQueryEntity = User.builder()
                .id(LocalThreadHolder.getUserId())
                .build();
        User user = userMapper.getByActive(userQueryEntity);
        if (user.getIsWord()) {
            return CommonResult.error("账户已被禁言");
        }
        saveEvaluations(evaluations);
        if (evaluations.getParentId() != null) { // 针对内容本身的评论，不做处理
            List<CommentChildVO> commentChildVOS = getCommentChild(evaluations.getParentId());
            if (commentChildVOS.isEmpty()) {
                return CommonResult.success();
            }
            CommentChildVO commentChildVO = commentChildVOS.get(0);
            //自己评论自己，不做处理
            if (Objects.equals(evaluations.getReplierId(), LocalThreadHolder.getUserId())) {
                return CommonResult.success();
            }
            List<Message> messageList = new ArrayList<>();
            Message message = createMessage(evaluations.getContentId());
            // 如果是一级评论，那么消息的接收者就是评论数据的发布者
            if (evaluations.getReplierId() == null) {
                message.setReceiverId(commentChildVO.getUserId());
            } else { // 如果是二级评论，消息的接收者就是携带过来的被回复者ID
                message.setReceiverId(evaluations.getReplierId());
            }
            message.setContent(evaluations.getParentId() + ";" + evaluations.getContentId() + ";" + evaluations.getContent());
            messageList.add(message);
            messageMapper.batchSave(messageList);
        }
        return CommonResult.success("评论成功");
    }

    /**
     * 构造消息体
     *
     * @param contentId 内容ID
     * @return Message
     */
    private Message createMessage(Integer contentId) {
        Message message = new Message();
        // 消息的发送者是评论者本人
        message.setSenderId(LocalThreadHolder.getUserId());
        // 初始消息未读
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        // 评论消息
        message.setMessageType(MessageType.EVALUATIONS_BY_REPLY.getType());
        // 评论时间
        message.setCreateTime(LocalDateTime.now());
        // 关联的内容ID
        message.setContentId(contentId);
        return message;
    }

    /**
     * 查询全部评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(contentId, contentType);
        setUpvoteFlag(parentComments);
        Integer count = evaluationsMapper.totalCount(contentId, contentType);
        return CommonResult.success(new EvaluationsVO(count, parentComments));
    }

    /**
     * 设置点赞状态
     *
     * @param parentComments 评论数据列表
     */
    private void setUpvoteFlag(List<CommentParentVO> parentComments) {
        String userId = LocalThreadHolder.getUserId().toString(); // 预先获取用户ID
        parentComments.forEach(parentComment -> {
            parentComment.setUpvoteFlag(isUserUpvote(parentComment.getUpvoteList(), userId));
            parentComment.setUpvoteCount(countVotes(parentComment.getUpvoteList()));
            Optional.ofNullable(parentComment.getCommentChildVOS())
                    .orElse(Collections.emptyList())
                    .forEach(child -> {
                        child.setUpvoteFlag(isUserUpvote(child.getUpvoteList(), userId));
                        child.setUpvoteCount(countVotes(child.getUpvoteList()));
                    });
        });
    }

    /**
     * 判断用户是否已点赞
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @param userId  用户ID
     * @return 是否已点赞
     */
    private boolean isUserUpvote(String voteStr, String userId) {
        return Optional.ofNullable(voteStr)
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList())
                .contains(userId);
    }

    /**
     * 计算点赞数
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @return 点赞数
     */
    private int countVotes(String voteStr) {
        return Optional.ofNullable(voteStr)
                .map(s -> s.split(",").length)
                .orElse(0);
    }

    /**
     * 分页查询评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> query(EvaluationsQueryDto evaluationsQueryDto) {
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsQueryDto);
        Integer totalPage = evaluationsMapper.queryCount(evaluationsQueryDto);
        return CommonPageResult.success(list, totalPage);
    }

    /**
     * 批量删除评论数据
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return CommonResult.success();
    }

    /**
     * 评论删除
     *
     * @return Result<String>
     */
    @Override
    public Result<String> delete(Integer id) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        evaluationsMapper.batchDelete(ids);
        return CommonResult.success();
    }

    /**
     * 评论修改
     *
     * @return Result<Map < String, Object>>
     */
    @Override
    public Result<Map<String, Object>> update(Evaluations evaluations) {
        EvaluationsQueryDto evaluationsQueryDto = new EvaluationsQueryDto();
        evaluationsQueryDto.setId(evaluations.getId());
        List<CommentChildVO> commentChildVOS = evaluationsMapper.query(evaluationsQueryDto);
        if (commentChildVOS.isEmpty()) {
            return CommonResult.success();
        }
        CommentChildVO commentChildVO = commentChildVOS.get(0);
        String upvoteList = commentChildVO.getUpvoteList();
        String userId = String.valueOf(LocalThreadHolder.getUserId());
        upvoteList = (upvoteList == null || upvoteList.isEmpty()) ? userId : toggleUpvote(upvoteList, userId);
        Evaluations evaluationsUpdate = new Evaluations();
        evaluationsUpdate.setId(evaluations.getId());
        if (upvoteList.contains(userId)){
            evaluationsUpdate.setUpvoteList(upvoteList);
        }
        evaluationsMapper.update(evaluations);
        upvoteMessageDeliver(commentChildVO);
        Map<String, Object> result = new HashMap<>();
        // 更新之后的点赞量
        result.put("num", upvoteList.split(",").length);
        // 包含，本次为点赞，反之为取消点赞
        result.put("flag", upvoteList.contains(userId));
        return CommonResult.success(result);
    }

    /**
     * 点赞消息推送
     * 关键点：
     * 1. 什么用户点赞了什么评论内容
     * 2. 注意重复推送问题
     * 3. 自己点赞自己不能进行推送
     *
     * @param commentChildVO 评论数据
     */
    private void upvoteMessageDeliver(CommentChildVO commentChildVO) {
        String userId = String.valueOf(LocalThreadHolder.getUserId());
        // 自己点赞自己的评论，不做通知
        if (userId.equals(String.valueOf(commentChildVO.getUserId()))) {
            return;
        }
        // 不做重复通知
        MessageQueryDto messageQueryDto = new MessageQueryDto();
        messageQueryDto.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        messageQueryDto.setContentId(commentChildVO.getContentId());
        messageQueryDto.setUserId(commentChildVO.getUserId());
        messageQueryDto.setSenderId(LocalThreadHolder.getUserId());
        Integer queryCount = messageMapper.queryCount(messageQueryDto);
        if (queryCount != 0) { // 已经通知过，情况为用户点赞之后-取消点赞-在次点赞，不做重复通知
            return;
        }
        Message message = new Message();
        message.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setReceiverId(commentChildVO.getUserId());
        message.setCreateTime(LocalDateTime.now());
        message.setContentId(commentChildVO.getId());
        message.setSenderId(LocalThreadHolder.getUserId());
        message.setContent("点赞了你的评论【" + commentChildVO.getContent() + "】");
        messageMapper.batchSave(Collections.singletonList(message));
    }

    /**
     * 点赞字符串处理
     *
     * @param upvoteList 点赞字符串
     * @param userId     用户ID
     */
    public String toggleUpvote(String upvoteList, String userId) {
        // 将 upvoteList 转换为 Set 集合，方便进行添加和删除操作，并且自动去重
        Set<String> upvoteSet = new HashSet<>();
        if (!upvoteList.isEmpty()) {
            upvoteSet.addAll(Arrays.asList(upvoteList.split(",")));
        }
        if (upvoteSet.contains(userId)) { // 取消点赞
            upvoteSet.remove(userId);
        } else { // 点赞
            upvoteSet.add(userId);
        }
        // 将 Set 集合转换回字符串并更新 upvoteList
        return String.join(",", upvoteSet);
    }

}
