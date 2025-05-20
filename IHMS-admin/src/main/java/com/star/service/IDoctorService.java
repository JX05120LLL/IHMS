package com.star.service;

import com.star.common.Result;
import com.star.model.dto.query.DoctorQueryDto;
import com.star.model.dto.update.DoctorLoginDTO;
import com.star.model.entity.Doctor;

import java.util.List;

/**
 * 医生服务接口
 */
public interface IDoctorService {

    /**
     * 医生登录
     * 
     * @param doctorLoginDTO 登录参数
     * @return 登录结果
     */
    Result<Object> login(DoctorLoginDTO doctorLoginDTO);

    /**
     * 获取医生列表
     * 
     * @return 医生列表
     */
    Result<List<Doctor>> getDoctorList();
    
    /**
     * 获取医生详情
     * 
     * @param id 医生ID
     * @return 医生详情
     */
    Result<Doctor> getDoctorDetail(Integer id);
    
    /**
     * 按专业查询医生
     * 
     * @param specialty 专业领域
     * @return 医生列表
     */
    Result<List<Doctor>> getDoctorsBySpecialty(String specialty);
    
    /**
     * 添加医生
     * 
     * @param doctor 医生信息
     * @return 添加结果
     */
    Result<Boolean> addDoctor(Doctor doctor);
    
    /**
     * 更新医生信息
     * 
     * @param doctor 医生信息
     * @return 更新结果
     */
    Result<Boolean> updateDoctor(Doctor doctor);
    
    /**
     * 更新医生状态
     * 
     * @param id 医生ID
     * @param status 状态
     * @return 更新结果
     */
    Result<Boolean> updateDoctorStatus(Integer id, Integer status);
    
    /**
     * 查询医生数据
     * 
     * @param doctorQueryDto 查询条件
     * @return 医生列表或分页结果
     */
    Result<Object> query(DoctorQueryDto doctorQueryDto);
    
    /**
     * 删除医生
     * 
     * @param id 医生ID
     * @return 删除结果
     */
    Result<Boolean> deleteDoctor(Integer id);
} 