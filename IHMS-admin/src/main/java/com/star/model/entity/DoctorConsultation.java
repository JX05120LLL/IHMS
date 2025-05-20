package com.star.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 医患会话实体类
 */
@Data
@TableName("doctor_consultation")
public class DoctorConsultation {
    
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 患者用户ID
     */
    private Integer patientId;
    
    /**
     * 医生ID
     */
    private Integer doctorId;
    
    /**
     * 咨询主题
     */
    private String title;
    
    /**
     * 状态(0:进行中,1:已结束)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 最后更新时间
     */
    private Date updateTime;
} 