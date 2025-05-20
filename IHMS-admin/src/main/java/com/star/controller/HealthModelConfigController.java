package com.star.controller;

import com.star.annotation.Pager;
import com.star.annotation.Protector;
import com.star.common.Result;
import com.star.model.dto.query.extend.HealthModelConfigQueryDto;
import com.star.model.entity.HealthModelConfig;
import com.star.model.vo.HealthModelConfigVO;
import com.star.service.IHealthModelConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 健康模型的 Controller
 */
@RestController
@RequestMapping(value = "/health-model-config")
public class HealthModelConfigController {

    @Resource
    private IHealthModelConfigService healthModelConfigService;

    /**
     * 健康模型新增
     *
     * @param healthModelConfig 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody HealthModelConfig healthModelConfig) {
        // 这是给予用户新增模型的，用户新增的模型不是全局模型
        healthModelConfig.setIsGlobal(false);
        return healthModelConfigService.save(healthModelConfig);
    }

    /**
     * 健康模型新增(管理员)
     *
     * @param healthModelConfig 新增数据
     * @return Result<Void> 通用响应体
     */
    @Protector(role = "管理员") // 加上这行注解，只有管理员有权操作这个接口
    @PostMapping(value = "config/save")
    public Result<Void> configSave(@RequestBody HealthModelConfig healthModelConfig) {
        // 设置为全局配置
        healthModelConfig.setIsGlobal(true);
        return healthModelConfigService.save(healthModelConfig);
    }

    /**
     * 健康模型删除
     *
     * @param ids 要删除的健康模型ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return healthModelConfigService.batchDelete(ids);
    }

    /**
     * 健康模型修改
     *
     * @param healthModelConfig 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody HealthModelConfig healthModelConfig) {
        return healthModelConfigService.update(healthModelConfig);
    }

    /**
     * 查询用户自己配置的模型及全局模型
     *
     * @return Result<List < HealthModelConfigVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/modelList")
    public Result<List<HealthModelConfigVO>> modelList() {
        return healthModelConfigService.modelList();
    }

    /**
     * 健康模型查询
     *
     * @param healthModelConfigQueryDto 查询参数
     * @return Result<List < HealthModelConfigVO>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<HealthModelConfigVO>> query(@RequestBody HealthModelConfigQueryDto healthModelConfigQueryDto) {
        return healthModelConfigService.query(healthModelConfigQueryDto);
    }


}
