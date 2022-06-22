package com.shupian.pvms.service;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shupian.pvms.domain.vo.AdminLoginVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author 19165
* @description 针对表【admin】的数据库操作Service
* @createDate 2022-05-31 14:43:48
*/
public interface AdminService extends IService<Admin> {

    ResponseResult login(AdminLoginVo adminLoginVo, HttpServletRequest request);

    ResponseResult logout(HttpServletRequest request);

    ResponseResult getCaptchaImage();
}
