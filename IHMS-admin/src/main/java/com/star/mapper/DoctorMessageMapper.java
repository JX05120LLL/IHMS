package com.star.mapper;

import com.star.model.entity.DoctorMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医患消息Mapper接口
 */
@Mapper
public interface DoctorMessageMapper extends BaseMapper<DoctorMessage> {
} 