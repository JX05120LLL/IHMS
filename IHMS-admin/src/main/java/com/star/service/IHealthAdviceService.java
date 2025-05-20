package com.star.service;

import com.star.model.entity.HealthAdvice;
import com.star.model.entity.HealthRiskAssessment;

import java.util.List;

/**
 * 健康建议服务接口
 */
public interface IHealthAdviceService {
    
    /**
     * 根据健康风险评估生成建议
     * @param userId 用户ID
     * @param riskAssessment 健康风险评估
     * @return 健康建议列表
     */
    List<HealthAdvice> generateAdviceFromRisk(Integer userId, HealthRiskAssessment riskAssessment);
    
    /**
     * 获取用户的所有健康建议
     * @param userId 用户ID
     * @return 健康建议列表
     */
    List<HealthAdvice> getUserAdvices(Integer userId);
    
    /**
     * 标记建议为已读
     * @param adviceId 建议ID
     * @return 是否成功
     */
    boolean markAdviceAsRead(Integer adviceId);
    
    /**
     * 标记建议为已执行
     * @param adviceId 建议ID
     * @return 是否成功
     */
    boolean markAdviceAsImplemented(Integer adviceId);
} 