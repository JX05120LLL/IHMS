package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.NewsQueryDto;
import com.star.model.entity.News;
import com.star.model.vo.NewsVO;

import java.util.List;

/**
 * 健康资讯业务逻辑接口
 */
public interface INewsService {

    Result<Void> save(News news);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(News news);

    Result<List<NewsVO>> query(NewsQueryDto newsQueryDto);

}
