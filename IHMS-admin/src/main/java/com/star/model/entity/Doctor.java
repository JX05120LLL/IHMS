package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 医生信息实体类
 */
@Data
@TableName("doctor")
public class Doctor {
    
    /**
     * 医生ID
     */
    @TableId
    private Integer id;
    
    /**
     * 关联用户ID
     */
    private Integer userId;
    
    /**
     * 医生姓名
     */
    private String name;
    
    /**
     * 登录账号
     */
    private String account;
    
    /**
     * 登录密码
     */
    private String password;
    
    /**
     * 医生头像
     */
    private String avatar;
    
    /**
     * 专业领域
     */
    private String specialty;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 所属医院
     */
    private String hospital;
    
    /**
     * 个人简介
     */
    private String description;
    
    /**
     * 状态(0:停诊,1:接诊)
     */
    private Integer status;
    
    /**
     * 咨询次数
     */
    private Integer consultationCount;
    
    /**
     * 创建时间
     */
    private Date createTime;
} 