package com.star.controller;

import com.star.model.entity.HealthRiskAssessment;
import com.star.model.entity.UserProfile;
import com.star.service.IHealthAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 健康分析控制器
 */
@RestController
@RequestMapping("/api/health/analysis")
public class HealthAnalysisController {
    
    @Autowired
    private IHealthAnalysisService healthAnalysisService;
    
    /**
     * 分析用户健康风险
     */
    @GetMapping("/risk/{userId}")
    public ResponseEntity<HealthRiskAssessment> analyzeHealthRisk(@PathVariable Integer userId) {
        HealthRiskAssessment assessment = healthAnalysisService.analyzeHealthRisk(userId);
        if (assessment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assessment);
    }
    
    /**
     * 生成或获取用户画像
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Integer userId) {
        UserProfile profile = healthAnalysisService.generateUserProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }
    
    /**
     * 预测健康指标趋势
     */
    @GetMapping("/predict/{userId}/{metricType}")
    public ResponseEntity<String> predictHealthTrend(
            @PathVariable Integer userId,
            @PathVariable Integer metricType,
            @RequestParam(defaultValue = "7") Integer days) {
        
        boolean success = healthAnalysisService.predictHealthTrend(userId, metricType, days);
        if (!success) {
            return ResponseEntity.badRequest().body("预测失败，请确保有足够的历史数据");
        }
        return ResponseEntity.ok("预测成功");
    }
    
    /**
     * 分析健康异常
     */
    @PostMapping("/anomalies/{userId}")
    public ResponseEntity<String> analyzeAnomalies(@PathVariable Integer userId) {
        healthAnalysisService.analyzeHealthAnomalies(userId);
        return ResponseEntity.ok("异常分析任务已提交");
    }
} 