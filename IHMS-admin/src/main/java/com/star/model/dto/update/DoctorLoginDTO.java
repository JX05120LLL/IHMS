package com.star.model.dto.update;

import lombok.Data;

/**
 * 医生登录DTO
 */
@Data
public class DoctorLoginDTO {
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码（已加密）
     */
    private String password;
} 