package com.star.controller;

import com.star.annotation.Pager;
import com.star.annotation.Protector;
import com.star.common.Result;
import com.star.model.dto.query.DoctorQueryDto;
import com.star.model.dto.update.DoctorLoginDTO;
import com.star.model.entity.Doctor;
import com.star.service.IDoctorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 医生控制器
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Resource
    private IDoctorService doctorService;

    /**
     * 医生登录
     *
     * @param doctorLoginDTO 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    @ResponseBody
    public Result<Object> login(@RequestBody DoctorLoginDTO doctorLoginDTO) {
        return doctorService.login(doctorLoginDTO);
    }

    /**
     * 获取医生列表
     * 
     * @return 医生列表
     */
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Doctor>> getDoctorList() {
        return doctorService.getDoctorList();
    }

    /**
     * 获取医生详情
     * 
     * @param id 医生ID
     * @return 医生详情
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Result<Doctor> getDoctorDetail(@PathVariable Integer id) {
        return doctorService.getDoctorDetail(id);
    }

    /**
     * 按专业查询医生
     * 
     * @param specialty 专业领域
     * @return 医生列表
     */
    @GetMapping("/specialty/{specialty}")
    @ResponseBody
    public Result<List<Doctor>> getDoctorsBySpecialty(@PathVariable String specialty) {
        return doctorService.getDoctorsBySpecialty(specialty);
    }

    /**
     * 添加医生(管理员)
     * 
     * @param doctor 医生信息
     * @return 添加结果
     */
    @Protector(role = "管理员")
    @PostMapping("/add")
    @ResponseBody
    public Result<Boolean> addDoctor(@RequestBody Doctor doctor) {
        return doctorService.addDoctor(doctor);
    }

    /**
     * 更新医生信息
     * 
     * @param doctor 医生信息
     * @return 更新结果
     */
    @Protector(role = "管理员")
    @PutMapping("/update")
    @ResponseBody
    public Result<Boolean> updateDoctor(@RequestBody Doctor doctor) {
        return doctorService.updateDoctor(doctor);
    }

    /**
     * 更新医生状态
     * 
     * @param id     医生ID
     * @param status 状态
     * @return 更新结果
     */
    @Protector(role = "管理员")
    @PutMapping("/status/{id}/{status}")
    @ResponseBody
    public Result<Boolean> updateDoctorStatus(@PathVariable Integer id, @PathVariable Integer status) {
        return doctorService.updateDoctorStatus(id, status);
    }

    /**
     * 查询医生数据(管理员)
     * 
     * @param doctorQueryDto 查询条件
     * @return 医生列表或分页结果
     */
    @Pager
    @Protector(role = "管理员")
    @PostMapping("/query")
    @ResponseBody
    public Result<Object> query(@RequestBody DoctorQueryDto doctorQueryDto) {
        return doctorService.query(doctorQueryDto);
    }

    /**
     * 删除医生(管理员)
     * 
     * @param id 医生ID
     * @return 删除结果
     */
    @Protector(role = "管理员")
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> deleteDoctor(@PathVariable Integer id) {
        return doctorService.deleteDoctor(id);
    }

    /**
     * 获取医生详情(通过detail路径)
     * 
     * @param id 医生ID
     * @return 医生详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public Result<Doctor> getDoctorDetailByDetailPath(@PathVariable Integer id) {
        return doctorService.getDoctorDetail(id);
    }

    /**
     * 搜索医生(公开)
     * 
     * @param doctorQueryDto 查询条件
     * @return 医生列表或分页结果
     */
    @PostMapping("/search")
    @ResponseBody
    public Result<Object> searchDoctors(@RequestBody DoctorQueryDto doctorQueryDto) {
        return doctorService.query(doctorQueryDto);
    }
} 