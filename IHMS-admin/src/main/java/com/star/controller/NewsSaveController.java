package com.star.controller;

import com.star.annotation.Pager;
import com.star.context.LocalThreadHolder;
import com.star.common.Result;
import com.star.model.dto.query.extend.NewsSaveQueryDto;
import com.star.model.entity.NewsSave;
import com.star.model.vo.NewsSaveVO;
import com.star.service.INewsSaveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资讯收藏的 Controller
 */
@RestController
@RequestMapping(value = "/news-save")
public class NewsSaveController {

    @Resource
    private INewsSaveService newsSaveService;

    /**
     * 资讯收藏或取消收藏
     *
     * @param newsSave 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/operation")
    public Result<Void> operation(@RequestBody NewsSave newsSave) {
        return newsSaveService.operation(newsSave);
    }

    /**
     * 收藏或取消收藏操作
     *
     * @param newsSave 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody NewsSave newsSave) {
        return newsSaveService.save(newsSave);
    }


    /**
     * 资讯收藏删除
     *
     * @param ids 要删除的资讯收藏ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return newsSaveService.batchDelete(ids);
    }

    /**
     * 查询用户收藏的健康资讯
     *
     * @param newsSaveQueryDto 查询参数
     * @return Result<List < NewsSaveVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/queryUser")
    public Result<List<NewsSaveVO>> queryUser(@RequestBody NewsSaveQueryDto newsSaveQueryDto) {
        newsSaveQueryDto.setUserId(LocalThreadHolder.getUserId());
        return newsSaveService.query(newsSaveQueryDto);
    }

    /**
     * 资讯收藏查询
     *
     * @param newsSaveQueryDto 查询参数
     * @return Result<List < NewsSaveVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<NewsSaveVO>> query(@RequestBody NewsSaveQueryDto newsSaveQueryDto) {
        return newsSaveService.query(newsSaveQueryDto);
    }

}
