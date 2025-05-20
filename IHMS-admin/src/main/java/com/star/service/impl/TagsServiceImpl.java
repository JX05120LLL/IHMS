package com.star.service.impl;

import com.star.mapper.TagsMapper;
import com.star.common.CommonResult;
import com.star.common.CommonPageResult;
import com.star.common.Result;
import com.star.model.dto.query.extend.TagsQueryDto;
import com.star.model.entity.Tags;
import com.star.service.ITagsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签业务逻辑实现
 */
@Service
public class TagsServiceImpl implements ITagsService {

    @Resource
    private TagsMapper tagsMapper;

    /**
     * 标签新增
     *
     * @param tags 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Tags tags) {
        tagsMapper.save(tags);
        return CommonResult.success();
    }

    /**
     * 标签删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        tagsMapper.batchDelete(ids);
        return CommonResult.success();
    }

    /**
     * 标签修改
     *
     * @param tags 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Tags tags) {
        tagsMapper.update(tags);
        return CommonResult.success();
    }

    /**
     * 标签查询
     *
     * @param tagsQueryDto 查询参数
     * @return Result<List < Tags>>
     */
    @Override
    public Result<List<Tags>> query(TagsQueryDto tagsQueryDto) {
        List<Tags> tagsList = tagsMapper.query(tagsQueryDto);
        Integer totalCount = tagsMapper.queryCount(tagsQueryDto);
        return CommonPageResult.success(tagsList, totalCount);
    }


}
