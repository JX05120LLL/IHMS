package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.EvaluationsQueryDto;
import com.star.model.entity.Evaluations;

import java.util.List;
import java.util.Map;

/**
 * 评论服务接口
 */
public interface IEvaluationsService {

    Result<Object> insert(Evaluations evaluations);

    Result<Object> list(Integer contentId, String contentType);

    Result<Object> query(EvaluationsQueryDto evaluationsQueryDto);

    Result<Object> batchDelete(List<Integer> ids);

    Result<String> delete(Integer id);

    Result<Map<String,Object>> update(Evaluations evaluations);

}
