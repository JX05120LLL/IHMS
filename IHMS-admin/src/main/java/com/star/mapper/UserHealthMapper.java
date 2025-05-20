package com.star.mapper;

import com.star.model.dto.query.extend.UserHealthQueryDto;
import com.star.model.entity.UserHealth;
import com.star.model.vo.UserHealthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户健康记录持久化接口
 */
@Mapper
public interface UserHealthMapper {

    void batchSave(List<UserHealth> userHealths);

    void update(UserHealth userHealth);

    void batchDelete(@Param(value = "ids") List<Long> ids);

    List<UserHealthVO> query(UserHealthQueryDto userHealthQueryDto);

    Integer queryCount(UserHealthQueryDto userHealthQueryDto);

}
