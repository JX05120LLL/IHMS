package com.star.service.impl;

import com.star.context.LocalThreadHolder;
import com.star.context.UserContext;
import com.star.mapper.DoctorMapper;
import com.star.common.Result;
import com.star.common.ResultCodeEnum;
import com.star.model.dto.query.DoctorQueryDto;
import com.star.model.dto.update.DoctorLoginDTO;
import com.star.model.entity.Doctor;
import com.star.service.IDoctorService;
import com.star.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 医生服务实现类
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements IDoctorService {

    @Override
    public Result<Object> login(DoctorLoginDTO doctorLoginDTO) {
        // 通过账号查询医生
        Doctor doctor = this.getOne(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getAccount, doctorLoginDTO.getAccount())
                .eq(Doctor::getStatus, 1));
        
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.ACCOUNT_NOT_EXIST, "账号不存在");
        }
        
        // 验证密码
        if (!Objects.equals(doctorLoginDTO.getPassword(), doctor.getPassword())) {
            return Result.failure(ResultCodeEnum.PASSWORD_ERROR, "密码错误");
        }
        
        // 生成token
        String token = JwtUtil.toToken(doctor.getId(), 3); // 3代表医生角色
        
        // 构建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("id", doctor.getId());
        resultMap.put("name", doctor.getName());
        resultMap.put("account", doctor.getAccount());
        resultMap.put("specialty", doctor.getSpecialty());
        resultMap.put("title", doctor.getTitle());
        resultMap.put("hospital", doctor.getHospital());
        resultMap.put("avatar", doctor.getAvatar());
        resultMap.put("introduction", doctor.getDescription());
        
        return Result.success(resultMap);
    }

    /**
     * 获取医生列表
     * @return
     */
    @Override
    public Result<List<Doctor>> getDoctorList() {
        // 进行判断，如果医生状态为停诊0，则不显示
        List<Doctor> list = this.list(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getStatus, 1)
                .orderByDesc(Doctor::getConsultationCount));
        return Result.success(list);
    }

    @Override
    public Result<Doctor> getDoctorDetail(Integer id) {
        Doctor doctor = this.getById(id);
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        return Result.success(doctor);
    }

    @Override
    public Result<List<Doctor>> getDoctorsBySpecialty(String specialty) {
        List<Doctor> list = this.list(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getStatus, 1)
                .eq(Doctor::getSpecialty, specialty)
                .orderByDesc(Doctor::getConsultationCount));
        return Result.success(list);
    }

    @Override
    public Result<Boolean> addDoctor(Doctor doctor) {
        doctor.setId(null);
        
        // 获取当前用户ID，如果不存在则设置一个默认值（管理员ID）
        Integer userId = null;
        if (UserContext.getUser() != null) {
            userId = UserContext.getUser().getId();
        } else {
            try {
                userId = LocalThreadHolder.getUserId();
            } catch (Exception e) {
                // 如果LocalThreadHolder也获取不到，则设置默认值为1（通常为管理员ID）
                userId = 1;
            }
        }
        
        doctor.setUserId(userId);
        doctor.setCreateTime(new Date());
        doctor.setConsultationCount(0);
        doctor.setStatus(1);
        boolean save = this.save(doctor);
        return Result.success(save);
    }

    @Override
    public Result<Boolean> updateDoctor(Doctor doctor) {
        Doctor exist = this.getById(doctor.getId());
        if (exist == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        boolean update = this.updateById(doctor);
        return Result.success(update);
    }

    @Override
    public Result<Boolean> updateDoctorStatus(Integer id, Integer status) {
        Doctor doctor = this.getById(id);
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        doctor.setStatus(status);
        boolean update = this.updateById(doctor);
        return Result.success(update);
    }

    @Override
    public Result<Object> query(DoctorQueryDto doctorQueryDto) {
        LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(doctorQueryDto.getName())) {
            queryWrapper.like(Doctor::getName, doctorQueryDto.getName());
        }
        if (StringUtils.hasText(doctorQueryDto.getSpecialty())) {
            queryWrapper.eq(Doctor::getSpecialty, doctorQueryDto.getSpecialty());
        }
        if (StringUtils.hasText(doctorQueryDto.getTitle())) {
            queryWrapper.eq(Doctor::getTitle, doctorQueryDto.getTitle());
        }
        if (StringUtils.hasText(doctorQueryDto.getHospital())) {
            queryWrapper.like(Doctor::getHospital, doctorQueryDto.getHospital());
        }
        if (Objects.nonNull(doctorQueryDto.getStatus())) {
            queryWrapper.eq(Doctor::getStatus, doctorQueryDto.getStatus());
        }
        
        queryWrapper.orderByDesc(Doctor::getConsultationCount);
        
        // 判断是否需要分页查询
        if (doctorQueryDto.getCurrent() != null && doctorQueryDto.getSize() != null) {
            // 创建分页对象，注意这里的 current 是当前页码，从1开始
            Page<Doctor> page = new Page<>(doctorQueryDto.getCurrent(), doctorQueryDto.getSize());
            
            // 执行分页查询
            Page<Doctor> resultPage = this.page(page, queryWrapper);
            
            // 创建结果集合
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("records", resultPage.getRecords());
            resultMap.put("total", resultPage.getTotal());
            resultMap.put("current", resultPage.getCurrent());
            resultMap.put("size", resultPage.getSize());
            
            return Result.success(resultMap);
        } else {
            // 不分页，查询所有
            List<Doctor> list = this.list(queryWrapper);
            return Result.success(list);
        }
    }

    @Override
    public Result<Boolean> deleteDoctor(Integer id) {
        Doctor doctor = this.getById(id);
        if (doctor == null) {
            return Result.failure(ResultCodeEnum.PARAM_ERROR, "医生不存在");
        }
        boolean removed = this.removeById(id);
        return Result.success(removed);
    }
} 