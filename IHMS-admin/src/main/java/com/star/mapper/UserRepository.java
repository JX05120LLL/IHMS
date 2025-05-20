package com.star.mapper;

import com.star.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资源库接口
 * 扩展BaseMapper以支持MybatisPlus操作
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
    // 继承了BaseMapper的基础CRUD方法
} 