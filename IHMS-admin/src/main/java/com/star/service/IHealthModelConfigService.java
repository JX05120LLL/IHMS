package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.HealthModelConfigQueryDto;
import com.star.model.entity.HealthModelConfig;
import com.star.model.vo.HealthModelConfigVO;

import java.util.List;

/**
 * 健康模型业务逻辑接口
 */
public interface IHealthModelConfigService {

    Result<Void> save(HealthModelConfig healthModelConfig);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(HealthModelConfig healthModelConfig);

    Result<List<HealthModelConfigVO>> query(HealthModelConfigQueryDto healthModelConfigQueryDto);

    Result<List<HealthModelConfigVO>> modelList();


}
