package com.star.controller;

import com.star.annotation.Pager;
import com.star.common.Result;
import com.star.model.dto.query.extend.TagsQueryDto;
import com.star.model.entity.Tags;
import com.star.service.ITagsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签的 Controller
 */
@RestController
@RequestMapping(value = "/tags")
public class TagsController {

    @Resource
    private ITagsService tagsService;

    /**
     * 标签新增
     *
     * @param tags 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Tags tags) {
        return tagsService.save(tags);
    }

    /**
     * 标签删除
     *
     * @param ids 要删除的标签ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return tagsService.batchDelete(ids);
    }

    /**
     * 标签修改
     *
     * @param tags 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Tags tags) {
        return tagsService.update(tags);
    }

    /**
     * 标签查询
     *
     * @param tagsQueryDto 查询参数
     * @return Result<List < Tags>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Tags>> query(@RequestBody TagsQueryDto tagsQueryDto) {
        return tagsService.query(tagsQueryDto);
    }

}
