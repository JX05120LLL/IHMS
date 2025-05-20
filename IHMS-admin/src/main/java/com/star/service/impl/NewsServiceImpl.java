package com.star.service.impl;

import com.star.mapper.NewsMapper;
import com.star.common.CommonResult;
import com.star.common.CommonPageResult;
import com.star.common.Result;
import com.star.model.dto.query.extend.NewsQueryDto;
import com.star.model.entity.News;
import com.star.model.vo.NewsVO;
import com.star.service.INewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康资讯业务逻辑实现
 */
@Service
public class NewsServiceImpl implements INewsService {

    @Resource
    private NewsMapper newsMapper;

    /**
     * 健康资讯新增
     *
     * @param news 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(News news) {
        news.setCreateTime(LocalDateTime.now());
        newsMapper.save(news);
        return CommonResult.success();
    }

    /**
     * 健康资讯删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        newsMapper.batchDelete(ids);
        return CommonResult.success();
    }

    /**
     * 健康资讯修改
     *
     * @param news 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(News news) {
        newsMapper.update(news);
        return CommonResult.success();
    }

    /**
     * 健康资讯查询
     *
     * @param NewsQueryDto 查询参数
     * @return Result<List < NewsVO>>
     */
    @Override
    public Result<List<NewsVO>> query(NewsQueryDto NewsQueryDto) {
        List<NewsVO> NewsList = newsMapper.query(NewsQueryDto);
        Integer totalCount = newsMapper.queryCount(NewsQueryDto);
        return CommonPageResult.success(NewsList, totalCount);
    }


}
