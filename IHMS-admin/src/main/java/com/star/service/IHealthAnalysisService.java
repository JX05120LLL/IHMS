package com.star.service;

import com.star.model.entity.HealthRiskAssessment;
import com.star.model.entity.UserProfile;

/**
 * 健康分析服务接口
 */
public interface IHealthAnalysisService {
    
    /**
     * 分析用户健康风险
     * @param userId 用户ID
     * @return 健康风险评估结果
     */
    HealthRiskAssessment analyzeHealthRisk(Integer userId);
    
    /**
     * 生成或更新用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    UserProfile generateUserProfile(Integer userId);
    
    /**
     * 分析用户健康数据异常
     * @param userId 用户ID
     */
    void analyzeHealthAnomalies(Integer userId);
    
    /**
     * 预测用户健康指标趋势
     * @param userId 用户ID
     * @param metricType 指标类型
     * @param days 预测天数
     * @return 是否预测成功
     */
    boolean predictHealthTrend(Integer userId, Integer metricType, Integer days);
} 