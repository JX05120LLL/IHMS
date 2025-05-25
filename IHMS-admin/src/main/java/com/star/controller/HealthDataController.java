package com.star.controller;

import com.star.common.Result;
import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.vo.UserHealthVO;
import com.star.service.IUserHealthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 健康数据控制器
 * 为前端页面助手提供健康数据查询接口
 */
@Api(tags = "健康数据接口")
@RestController
@RequestMapping("/healthData")
public class HealthDataController {

    @Resource
    private IUserHealthService userHealthService;

    /**
     * 获取用户健康数据列表
     * 
     * @param userId 用户ID，可选，默认为当前登录用户
     * @return 健康数据列表
     */
    @ApiOperation("获取用户健康数据列表")
    @GetMapping("/list")
    public Result<List<UserHealthVO>> getHealthDataList(
            @ApiParam(value = "用户ID", required = false)
            @RequestParam(required = false) Integer userId) {
        
        UserHealthQueryDto queryDto = new UserHealthQueryDto();
        // 如果传入了用户ID，则使用该ID查询
        if (userId != null) {
            queryDto.setUserId(userId);
        }
        
        // 设置分页参数，获取最新的健康数据
        queryDto.setCurrent(1);
        queryDto.setSize(20);
        
        return userHealthService.query(queryDto);
    }
    
    /**
     * 获取用户最新健康指标
     * 
     * @param userId 用户ID
     * @return 健康指标数据
     */
    @ApiOperation("获取用户最新健康指标")
    @GetMapping("/latest")
    public Result<Map<String, Object>> getLatestHealthData(
            @ApiParam(value = "用户ID", required = true)
            @RequestParam Integer userId) {
        
        // 获取所有类型的健康数据
        List<String> dataTypes = Arrays.asList(
                "height", "weight", "bmi", "bloodPressure", 
                "heartRate", "bloodSugar", "bloodOxygen");
        
        Map<String, Object> healthData = userHealthService.getSpecificHealthData(userId, dataTypes);
        
        return Result.success(healthData);
    }
} 