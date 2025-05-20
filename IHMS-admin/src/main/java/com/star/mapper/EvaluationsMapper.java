package com.star.mapper;

import com.star.model.dto.query.extend.EvaluationsQueryDto;
import com.star.model.entity.Evaluations;
import com.star.model.vo.CommentChildVO;
import com.star.model.vo.CommentParentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论持久化接口
 */
public interface EvaluationsMapper {

    /**
     * 查询指定内容下的全部评论
     *
     * @param contentId   内容ID
     * @param contentType 内容类型
     * @return List<CommentParentVO>
     */
    List<CommentParentVO> getParentComments(@Param(value = "contentId") Integer contentId,
                                            @Param(value = "contentType") String contentType);

    /**
     * 分页查询评论
     *
     * @param evaluationsQueryDto 参数
     * @return List<CommentParentVO>
     */
    List<CommentChildVO> query(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * 分页查询评论总数
     *
     * @param evaluationsQueryDto 参数
     * @return List<CommentParentVO>
     */
    Integer queryCount(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * 查询全部二级评论
     *
     * @param ids ID列表
     * @return List<Integer>
     */
    List<Integer> selectChildComments(@Param(value = "ids") List<Integer> ids);

    /**
     * 批量删除
     *
     * @param ids ID列表
     */
    void batchDelete(@Param(value = "ids") List<Integer> ids);

    /**
     * 查询指定评论的数目
     *
     * @param contentId   内容ID
     * @param contentType 内容类型
     * @return Integer
     */
    Integer totalCount(Integer contentId, String contentType);

    /**
     * 评论新增
     *
     * @param evaluations 评论信息实体
     */
    void save(Evaluations evaluations);

    /**
     * 评论修改
     *
     * @param evaluations 评论实体
     */
    void update(Evaluations evaluations);

}
