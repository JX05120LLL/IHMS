package com.star.service.impl;

import com.star.mapper.HealthModelConfigMapper;
import com.star.mapper.NewsMapper;
import com.star.mapper.UserHealthMapper;
import com.star.mapper.UserMapper;
import com.star.common.CommonResult;
import com.star.common.Result;
import com.star.model.dto.query.extend.HealthModelConfigQueryDto;
import com.star.model.dto.query.extend.NewsQueryDto;
import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.dto.query.extend.UserQueryDto;
import com.star.model.vo.ChartVO;
import com.star.service.IViewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页可视化
 */
@Service
public class ViewsServiceImpl implements IViewsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private NewsMapper newsMapper;
    @Resource
    private HealthModelConfigMapper healthModelConfigMapper;
    @Resource
    private UserHealthMapper userHealthMapper;

    /**
     * 统计一些系统的基础数据
     *
     * @return Result<List < ChartVO>>
     */
    @Override
    public Result<List<ChartVO>> staticControls() {
        List<ChartVO> chartVOS = new ArrayList<>();
        // 1. 用户数
        UserQueryDto userQueryDto = new UserQueryDto();
        int userCount = userMapper.queryCount(userQueryDto);
        change(userCount, "存量用户", chartVOS);
        // 2. 资讯数
        NewsQueryDto newsQueryDto = new NewsQueryDto();
        int newsCount = newsMapper.queryCount(newsQueryDto);
        change(newsCount, "收录资讯", chartVOS);
        // 3. 健康模型数
        HealthModelConfigQueryDto queryDto = new HealthModelConfigQueryDto();
        int modelCount = healthModelConfigMapper.queryCount(queryDto);
        change(modelCount, "收录模型", chartVOS);
        // 4. 健康数据
        UserHealthQueryDto dto = new UserHealthQueryDto();
        int healthCount = userHealthMapper.queryCount(dto);
        change(healthCount, "健康数据", chartVOS);
        return CommonResult.success(chartVOS);
    }

    /**
     * 参数处理
     *
     * @param count    总数目
     * @param name     统计项
     * @param chartVOS 装它们的集合
     */
    private void change(Integer count, String name, List<ChartVO> chartVOS) {
        ChartVO chartVO = new ChartVO(name, count);
        chartVOS.add(chartVO);
    }


}
