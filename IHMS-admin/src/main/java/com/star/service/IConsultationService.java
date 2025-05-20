package com.star.service;

import com.star.common.Result;
import com.star.model.entity.DoctorConsultation;

import java.util.List;
import java.util.Map;

/**
 * 咨询会话服务接口
 */
public interface IConsultationService {

    /**
     * 创建咨询会话
     * 
     * @param consultation 会话信息
     * @return 会话ID
     */
    Result<Integer> createConsultation(DoctorConsultation consultation);
    
    /**
     * 获取会话列表
     * 
     * @param userId 用户ID
     * @param role 角色(1:患者,2:医生)
     * @return 会话列表
     */
    Result<List<DoctorConsultation>> getConsultationList(Integer userId, Integer role);
    
    /**
     * 获取会话详情
     * 
     * @param id 会话ID
     * @return 会话详情
     */
    Result<DoctorConsultation> getConsultationDetail(Integer id);
    
    /**
     * 结束咨询会话
     * 
     * @param id 会话ID
     * @return 结束结果
     */
    Result<Boolean> endConsultation(Integer id);
    
    /**
     * 获取医生的咨询列表（分页查询）
     * 
     * @param doctorId 医生ID
     * @param status 状态(0:进行中,1:已结束)
     * @param current 当前页码
     * @param size 每页大小
     * @return 咨询列表
     */
    Result<Object> getDoctorConsultationList(Integer doctorId, Integer status, long current, long size);
    
    /**
     * 获取医生咨询统计信息
     * 
     * @param doctorId 医生ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getDoctorConsultationStats(Integer doctorId);
} 