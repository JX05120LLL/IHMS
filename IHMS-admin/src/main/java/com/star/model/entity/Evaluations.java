package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Evaluations {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 父级评论ID
     */
    private Integer parentId;

    /**
     * 评论者ID
     */
    private Integer commenterId;

    /**
     * 回复者ID
     */
    private Integer replierId;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 内容ID
     */
    private Integer contentId;

    /**
     * 点赞信息列表(以","分割)
     */
    private String upvoteList;

    /**
     * 评论时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
