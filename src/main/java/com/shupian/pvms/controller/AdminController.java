package com.shupian.pvms.controller;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Admin;
import com.shupian.pvms.domain.vo.AdminLoginVo;
import com.shupian.pvms.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "管理员")
@RestController
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    private AdminService adminService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody AdminLoginVo adminLoginVo, HttpServletRequest request)
    {
        return adminService.login(adminLoginVo, request);
    }

    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public ResponseResult logout(HttpServletRequest request)
    {
        return adminService.logout(request);
    }

    @ApiOperation("获取图片验证码")
    @GetMapping("/captchaImage")
    public ResponseResult getCaptchaImage(HttpServletRequest request)
    {
        System.out.println(request.getContextPath());
        return adminService.getCaptchaImage();
    }
}
