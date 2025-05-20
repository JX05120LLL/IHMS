package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.entity.UserHealth;
import com.star.model.vo.ChartVO;
import com.star.model.vo.UserHealthVO;

import java.util.List;

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
}
