package com.star.mapper;

import com.star.model.entity.Doctor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生资源库接口
 * 扩展BaseMapper以支持MybatisPlus操作
 */
@Mapper
public interface DoctorRepository extends BaseMapper<Doctor> {
    // 继承了BaseMapper的基础CRUD方法
} 