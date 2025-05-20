package com.star.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 父级评论
 */
@Data
public class CommentParentVO {

    /**
     * 评论ID
     */
    private Integer id;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 内容ID
     */
    private Integer contentId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 回复框显示状态
     */
    private Boolean showReplyInput;
    /**
     * 一共拥有的子级评论数
     */
    private Integer childTotal;
    /**
     * 用户是否已经点赞
     */
    private Boolean upvoteFlag;
    /**
     * 点赞列表
     */
    private String upvoteList;
    /**
     * 点赞量
     */
    private Integer upvoteCount;
    /**
     * 评论时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 子级评论
     */
    private List<CommentChildVO> commentChildVOS;

}
