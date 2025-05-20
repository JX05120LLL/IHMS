package com.star.service;

import com.star.common.Result;
import com.star.model.dto.update.UserLoginDTO;
import com.star.model.dto.update.UserRegisterDTO;
import com.star.model.dto.update.UserUpdateDTO;
import com.star.model.dto.query.extend.UserQueryDto;
import com.star.model.entity.User;
import com.star.model.vo.ChartVO;
import com.star.model.vo.UserVO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务类
 */
public interface IUserService {
    Result<String> register(UserRegisterDTO userRegisterDTO);

    Result<Object> login(UserLoginDTO userLoginDTO);

    Result<UserVO> auth();

    Result<List<User>> query(UserQueryDto userQueryDto);

    Result<String> update(UserUpdateDTO userUpdateDTO);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> updatePwd(Map<String, String> map);

    Result<UserVO> getById(Integer id);

    Result<String> insert(UserRegisterDTO userRegisterDTO);

    Result<String> backUpdate(User user);

    Result<List<ChartVO>> daysQuery(Integer day);
    
    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    Result<User> getUserDetail(Integer id);
}
