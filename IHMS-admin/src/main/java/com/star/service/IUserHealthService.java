package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.entity.UserHealth;
import com.star.model.vo.ChartVO;
import com.star.model.vo.UserHealthVO;

import java.util.List;
import java.util.Map;

/**
 * 用户健康记录业务逻辑接口
 */
public interface IUserHealthService {

    Result<Void> save(List<UserHealth> userHealths);

    Result<Void> batchDelete(List<Long> ids);

    Result<Void> update(UserHealth userHealth);

    Result<List<UserHealthVO>> query(UserHealthQueryDto userHealthQueryDto);

    Result<List<ChartVO>> daysQuery(Integer day);

    /**
     * 通过ID获取健康记录
     *
     * @param id 健康记录ID
     * @return 健康记录数据
     */
    Object getHealthRecordById(Integer id);
    
    /**
     * 获取用户的所有健康记录
     *
     * @param userId 用户ID
     * @return 健康记录列表
     */
    List<UserHealth> getUserHealthRecords(Integer userId);
    
    /**
     * 获取用户指定类型的健康数据
     *
     * @param userId 用户ID
     * @param dataTypes 数据类型列表(例如："bloodPressure", "heartRate", "bloodSugar"等)
     * @return 健康数据Map
     */
    Map<String, Object> getSpecificHealthData(Integer userId, List<String> dataTypes);
}
