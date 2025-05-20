package com.star.service.impl;

import com.star.mapper.DoctorConsultationMapper;
import com.star.mapper.DoctorMapper;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.entity.Doctor;
import com.star.model.entity.DoctorConsultation;
import com.star.service.IConsultationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 咨询会话服务实现类
 */
@Service
public class IConsultationServiceImpl extends ServiceImpl<DoctorConsultationMapper, DoctorConsultation> implements IConsultationService {

    @Resource
    private DoctorMapper doctorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> createConsultation(DoctorConsultation consultation) {
        // 验证医生ID是否有效
        if (consultation.getDoctorId() == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生ID不能为空");
        }
        
        // 验证医生是否存在
        Doctor doctor = doctorMapper.selectById(consultation.getDoctorId());
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        
        // 验证咨询标题
        if (consultation.getTitle() == null || consultation.getTitle().trim().isEmpty()) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "咨询主题不能为空");
        }
        
        // 设置ID为null，让数据库自动生成
        consultation.setId(null);
        // 设置创建时间和状态
        consultation.setCreateTime(new Date());
        consultation.setUpdateTime(new Date());
        consultation.setStatus(0); // 0:进行中
        
        // 创建一个新的对象来保存，避免可能的ID传递问题
        DoctorConsultation newConsultation = new DoctorConsultation();
        newConsultation.setDoctorId(consultation.getDoctorId());
        newConsultation.setPatientId(consultation.getPatientId());
        newConsultation.setTitle(consultation.getTitle());
        newConsultation.setStatus(0);
        newConsultation.setCreateTime(new Date());
        newConsultation.setUpdateTime(new Date());
        
        // 保存会话
        boolean save = this.save(newConsultation);
        if (!save) {
            return Result.failure(ResultCodeEnum.BUSINESS_ERROR, "创建会话失败");
        }
        
        // 更新医生咨询次数
        doctor.setConsultationCount(doctor.getConsultationCount() + 1);
        doctorMapper.updateById(doctor);
        
        return Result.success(newConsultation.getId());
    }

    @Override
    public Result<List<DoctorConsultation>> getConsultationList(Integer userId, Integer role) {
        LambdaQueryWrapper<DoctorConsultation> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按角色查询会话
        if (role == 1) { // 患者
            queryWrapper.eq(DoctorConsultation::getPatientId, userId);
        } else if (role == 2) { // 医生
            queryWrapper.eq(DoctorConsultation::getDoctorId, userId);
        }
        
        queryWrapper.orderByDesc(DoctorConsultation::getUpdateTime);
        
        List<DoctorConsultation> list = this.list(queryWrapper);
        return Result.success(list);
    }

    @Override
    public Result<DoctorConsultation> getConsultationDetail(Integer id) {
        DoctorConsultation consultation = this.getById(id);
        if (consultation == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "会话不存在");
        }
        return Result.success(consultation);
    }

    @Override
    public Result<Boolean> endConsultation(Integer id) {
        DoctorConsultation consultation = this.getById(id);
        if (consultation == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "会话不存在");
        }
        
        consultation.setStatus(1); // 1:已结束
        consultation.setUpdateTime(new Date());
        
        boolean update = this.updateById(consultation);
        return Result.success(update);
    }
    
    @Override
    public Result<Object> getDoctorConsultationList(Integer doctorId, Integer status, long current, long size) {
        // 构建查询条件
        LambdaQueryWrapper<DoctorConsultation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DoctorConsultation::getDoctorId, doctorId);
        
        // 如果状态不为空，添加状态条件
        if (status != null) {
            queryWrapper.eq(DoctorConsultation::getStatus, status);
        }
        
        // 按更新时间倒序排序
        queryWrapper.orderByDesc(DoctorConsultation::getUpdateTime);
        
        // 创建分页对象
        Page<DoctorConsultation> page = new Page<>(current, size);
        
        // 执行分页查询
        Page<DoctorConsultation> resultPage = this.page(page, queryWrapper);
        
        // 返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", resultPage.getTotal());
        resultMap.put("records", resultPage.getRecords());
        resultMap.put("current", resultPage.getCurrent());
        resultMap.put("size", resultPage.getSize());
        
        return Result.success(resultMap);
    }
    
    @Override
    public Result<Map<String, Object>> getDoctorConsultationStats(Integer doctorId) {
        // 验证医生是否存在
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        
        // 查询进行中的咨询数量
        LambdaQueryWrapper<DoctorConsultation> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(DoctorConsultation::getDoctorId, doctorId)
                     .eq(DoctorConsultation::getStatus, 0);
        long activeCount = this.count(activeWrapper);
        
        // 查询已结束的咨询数量
        LambdaQueryWrapper<DoctorConsultation> finishedWrapper = new LambdaQueryWrapper<>();
        finishedWrapper.eq(DoctorConsultation::getDoctorId, doctorId)
                       .eq(DoctorConsultation::getStatus, 1);
        long finishedCount = this.count(finishedWrapper);
        
        // 总咨询次数
        long totalCount = activeCount + finishedCount;
        
        // 查询该医生服务过的不同患者ID
        LambdaQueryWrapper<DoctorConsultation> patientWrapper = new LambdaQueryWrapper<>();
        patientWrapper.eq(DoctorConsultation::getDoctorId, doctorId)
                      .select(DoctorConsultation::getPatientId)
                      .groupBy(DoctorConsultation::getPatientId);
        List<DoctorConsultation> patientList = this.list(patientWrapper);
        int totalPatients = patientList.size();
        
        // 构建结果
        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("totalCount", totalCount);
        statsMap.put("activeConsultations", activeCount);
        statsMap.put("completedConsultations", finishedCount);
        statsMap.put("totalPatients", totalPatients);
        statsMap.put("doctor", doctor);
        
        return Result.success(statsMap);
    }
} 