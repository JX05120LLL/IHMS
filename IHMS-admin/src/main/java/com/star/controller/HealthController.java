package com.star.controller;

import com.star.annotation.Protector;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.vo.UserHealthVO;
import com.star.service.IUserHealthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 健康记录控制器
 * 用于医生咨询功能中获取和分享健康记录
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Resource
    private IUserHealthService userHealthService;

    /**
     * 获取用户健康记录列表（用于医生咨询分享）
     *
     * @param userId 用户ID
     * @return 健康记录列表
     */
    @Protector
    @GetMapping("/record/list")
    @ResponseBody
    public Result<List<UserHealthVO>> getHealthRecordList(@RequestParam Integer userId) {
        UserHealthQueryDto queryDto = new UserHealthQueryDto();
        queryDto.setUserId(userId);
        return userHealthService.query(queryDto);
    }
    
    /**
     * 获取用户健康数据列表 - 为页面助手提供的接口
     */
    @GetMapping("/data/list")
    @ResponseBody
    public Result<List<UserHealthVO>> getHealthDataList(@RequestParam(required = false) Integer userId) {
        UserHealthQueryDto queryDto = new UserHealthQueryDto();
        // 如果没有提供userId，则获取当前登录用户的数据
        if (userId != null) {
            queryDto.setUserId(userId);
        }
        // 按时间降序排列，获取最新的健康数据
        return userHealthService.query(queryDto);
    }

    /**
     * 获取健康记录详情
     *
     * @param id 健康记录ID
     * @return 健康记录详情
     */
    @Protector
    @GetMapping("/record/detail/{id}")
    @ResponseBody
    public Result<UserHealthVO> getHealthRecordDetail(@PathVariable Integer id) {
        if (id == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "健康记录ID不能为空");
        }
        
        UserHealthQueryDto queryDto = new UserHealthQueryDto();
        queryDto.setId(id);
        
        Result<List<UserHealthVO>> result = userHealthService.query(queryDto);
        if (result == null || result.getData() == null) {
            return Result.failure(ResultCodeEnum.BUSINESS_ERROR, "查询健康记录失败");
        }
        
        List<UserHealthVO> records = result.getData();
        if (records.isEmpty()) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "未找到健康记录");
        }
        
        return Result.success(records.get(0));
    }
} 