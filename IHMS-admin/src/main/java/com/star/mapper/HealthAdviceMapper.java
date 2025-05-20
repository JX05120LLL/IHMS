package com.star.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.model.entity.HealthAdvice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 健康建议持久化接口
 */
@Mapper
public interface HealthAdviceMapper {

    /**
     * 插入健康建议
     * @param healthAdvice 健康建议
     * @return 受影响行数
     */
    int insert(HealthAdvice healthAdvice);
    
    /**
     * 更新健康建议
     * @param healthAdvice 健康建议
     * @return 受影响行数
     */
    int updateById(HealthAdvice healthAdvice);
    
    /**
     * 根据ID查询健康建议
     * @param id 健康建议ID
     * @return 健康建议
     */
    HealthAdvice selectById(Integer id);
    
    /**
     * 根据条件查询健康建议列表
     * @param queryWrapper 查询条件
     * @return 健康建议列表
     */
    List<HealthAdvice> selectList(LambdaQueryWrapper<HealthAdvice> queryWrapper);
} 