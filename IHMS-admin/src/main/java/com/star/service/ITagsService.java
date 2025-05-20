package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.TagsQueryDto;
import com.star.model.entity.Tags;

import java.util.List;

/**
 * 标签业务逻辑接口
 */
public interface ITagsService {

    Result<Void> save(Tags tags);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(Tags tags);

    Result<List<Tags>> query(TagsQueryDto tagsQueryDto);

}
