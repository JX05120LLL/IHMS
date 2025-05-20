package com.star.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class CommentChildVO {
    /**
     * 评论ID
     */
    private Integer id;
    /**
     * 父级评论ID
     */
    private Integer parentId;
    /**
     * 内容ID
     */
    private Integer contentId;
    /**
     * 评论者ID
     */
    private Integer userId;
    /**
     * 评论者用户名
     */
    private String userName;
    /**
     * 评论者用户头像
     */
    private String userAvatar;

    /**
     * 被回复者ID
     */
    private Integer replierId;

    /**
     * 被回复者用户名
     */
    private String replierName;

    /**
     * 被回复者头像
     */
    private String replierAvatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论回复状态
     */
    private Boolean replyInputStatus;

    /**
     * 点赞列表
     */
    private String upvoteList;

    /**
     * 用户是否已经点赞
     */
    private Boolean upvoteFlag;

    /**
     * 点赞量
     */
    private Integer upvoteCount;

    /**
     * 举报量
     */
    private Integer reportsNum;
    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 评论时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
