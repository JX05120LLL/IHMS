package com.star.mapper;

import com.star.model.entity.Doctor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生Mapper接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
} 