package com.star.model.dto.query;

import com.star.model.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医生查询DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DoctorQueryDto extends QueryDto {
    
    /**
     * 医生姓名
     */
    private String name;
    
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
     * 状态(0:停诊,1:接诊)
     */
    private Integer status;
} 