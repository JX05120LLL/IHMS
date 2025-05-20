package com.star.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康资讯实体
 */
@Data
public class News {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 资讯标题
     */
    private String name;
    /**
     * 资讯的内容
     */
    private String content;
    /**
     * 标签的ID
     */
    private Integer tagId;
    /**
     * 封面
     */
    private String cover;
    /**
     * 阅读者的ID列表，以“,”进行分割
     */
    private String readerIds;
    /**
     * 是否推荐
     */
    private Boolean isTop;
    /**
     * 发布时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
