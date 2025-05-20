package com.star.mapper;

import com.star.model.entity.AIModelLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI模型调用日志持久化接口
 */
@Mapper
public interface AIModelLogMapper {

    /**
     * 插入AI模型调用日志
     * @param aiModelLog 日志记录
     * @return 受影响行数
     */
    int insert(AIModelLog aiModelLog);
    
    /**
     * 根据ID查询日志
     * @param id 日志ID
     * @return AI模型调用日志
     */
    AIModelLog selectById(Long id);
} 