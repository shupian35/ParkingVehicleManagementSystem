package com.shupian.pvms.service;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shupian.pvms.domain.vo.UserSearchVo;

/**
* @author 19165
* @description 针对表【user】的数据库操作Service
* @createDate 2022-05-31 14:43:48
*/
public interface UserService extends IService<User> {

    ResponseResult getUserList(Integer pageNum, Integer pageSize);

    ResponseResult getSearchUsers(UserSearchVo userSearchVo);

    ResponseResult updateUser(User user);

    ResponseResult deleteUser(Long id);
}
