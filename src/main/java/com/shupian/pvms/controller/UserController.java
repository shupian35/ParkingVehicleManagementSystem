package com.shupian.pvms.controller;

import com.shupian.pvms.annotation.CheckToken;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.User;
import com.shupian.pvms.domain.vo.UserSearchVo;
import com.shupian.pvms.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "用户相关接口")
@RequestMapping("/user")
@RestController
public class UserController
{
    @Resource
    private UserService userService;

    @ApiOperation("查询所有用户信息")
    @GetMapping("/getUserList/{pageNum}/{pageSize}")
    @CheckToken
    public ResponseResult getUserList(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize)
    {

        return userService.getUserList(pageNum, pageSize);
    }

    @ApiOperation("按照条件查询用户信息")
    @PostMapping("/getSearchUsers")
    @CheckToken
    public ResponseResult getSearchUsers(@RequestBody UserSearchVo userSearchVo)
    {

        return userService.getSearchUsers(userSearchVo);
    }

    @PutMapping("/update")
    @CheckToken
    public ResponseResult updateUser(@RequestBody User user)
    {
        return userService.updateUser(user);
    }

    @DeleteMapping("/delete/{id}")
    @CheckToken
    public ResponseResult deleteUser(@PathVariable Long id)
    {
        return userService.deleteUser(id);
    }

}
