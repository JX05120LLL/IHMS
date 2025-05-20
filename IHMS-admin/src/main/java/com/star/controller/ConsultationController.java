package com.star.controller;

import com.star.annotation.Protector;
import com.star.context.UserContext;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.entity.DoctorConsultation;
import com.star.service.IConsultationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 咨询会话控制器
 */
@RestController
@RequestMapping("/consultation")
public class ConsultationController {

    @Resource
    private IConsultationService IConsultationService;

    /**
     * 创建咨询会话
     * 
     * @param consultation 会话信息
     * @return 会话ID
     */
    @Protector
    @PostMapping("/create")
    @ResponseBody
    public Result<Integer> createConsultation(@RequestBody DoctorConsultation consultation) {
        // 获取当前用户，添加空值检查
        if (UserContext.getUser() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "未能获取用户信息，请重新登录");
        }
        
        // 设置当前用户为患者
        consultation.setPatientId(UserContext.getUser().getId());
        return IConsultationService.createConsultation(consultation);
    }

    /**
     * 获取会话列表
     * 
     * @param userId 用户ID（可选，默认从上下文获取）
     * @param role 角色(1:患者,2:医生)
     * @return 会话列表
     */
    @Protector
    @GetMapping("/list")
    @ResponseBody
    public Result<List<DoctorConsultation>> getConsultationList(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") Integer role) {
        // 优先从上下文获取用户ID，如果为null则使用请求参数中的userId
        Integer currentUserId = null;
        if (UserContext.getUser() != null) {
            currentUserId = UserContext.getUser().getId();
        } else {
            currentUserId = userId;
        }
        
        if (currentUserId == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "未能获取用户ID");
        }
        return IConsultationService.getConsultationList(currentUserId, role);
    }

    /**
     * 获取会话详情
     * 
     * @param id 会话ID
     * @return 会话详情
     */
    @Protector
    @GetMapping("/detail/{id}")
    @ResponseBody
    public Result<DoctorConsultation> getConsultationDetail(@PathVariable Integer id) {
        return IConsultationService.getConsultationDetail(id);
    }

    /**
     * 结束咨询会话
     * 
     * @param id 会话ID
     * @return 结束结果
     */
    @Protector
    @PutMapping("/end/{id}")
    @ResponseBody
    public Result<Boolean> endConsultation(@PathVariable Integer id) {
        return IConsultationService.endConsultation(id);
    }
    
    /**
     * 获取医生的咨询列表（分页查询）
     * 
     * @param doctorId 医生ID
     * @param status 状态(0:进行中,1:已结束)
     * @param current 当前页码
     * @param size 每页大小
     * @return 咨询列表
     */
    @Protector
    @GetMapping("/doctor-list")
    @ResponseBody
    public Result<Object> getDoctorConsultationList(
            @RequestParam Integer doctorId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return IConsultationService.getDoctorConsultationList(doctorId, status, current, size);
    }
    
    /**
     * 获取医生咨询统计信息
     * 
     * @param doctorId 医生ID
     * @return 统计信息
     */
    @Protector
    @GetMapping("/doctor-stats/{doctorId}")
    @ResponseBody
    public Result<Map<String, Object>> getDoctorConsultationStats(@PathVariable Integer doctorId) {
        return IConsultationService.getDoctorConsultationStats(doctorId);
    }
} 