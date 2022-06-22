package com.shupian.pvms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Feedback;
import com.shupian.pvms.domain.entity.User;
import com.shupian.pvms.domain.vo.PageVo;
import com.shupian.pvms.domain.vo.UserSearchVo;
import com.shupian.pvms.domain.vo.UserVo;
import com.shupian.pvms.service.UserService;
import com.shupian.pvms.mapper.UserMapper;
import com.shupian.pvms.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
* @author 19165
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-05-31 14:43:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    @Override
    public ResponseResult getUserList(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.orderByDesc(User::getCreateTime);

        //List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        Page<User> page = new Page<>(pageNum, pageSize);
        page(page, userLambdaQueryWrapper);
        List<User> users = page.getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        PageVo<UserVo> userPageVo = new PageVo<>(page.getTotal(), userVos);
        return ResponseResult.okResult(userPageVo);
    }

    @Override
    public ResponseResult getSearchUsers(UserSearchVo userSearchVo) {
        String username = userSearchVo.getUsername();
        String nickName = userSearchVo.getNickName();
        String cardId = userSearchVo.getCardId();
        String endDate = userSearchVo.getEndDate();
        String startDate = userSearchVo.getStartDate();
        String gender = userSearchVo.getGender();
        String status = userSearchVo.getStatus();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(Objects.nonNull(username)&&username.length()>0,User::getUsername,userSearchVo.getUsername())
                .like(Objects.nonNull(nickName)&&nickName.length()>0,User::getNickName,userSearchVo.getNickName())
                .eq(Objects.nonNull(cardId)&&cardId.length()>0,User::getCardId,userSearchVo.getCardId())
                .eq(Objects.nonNull(gender)&&gender.length()>0,User::getSex,userSearchVo.getGender())
                .eq(Objects.nonNull(status)&&status.length()>0,User::getStatus,userSearchVo.getStatus())
                .between(Objects.nonNull(startDate)&&Objects.nonNull(startDate)&&startDate.length()>0&&endDate.length()>0,User::getCreateTime,userSearchVo.getStartDate(),userSearchVo.getEndDate());

        Page<User> page = new Page<>(userSearchVo.getPageNum(), userSearchVo.getPageSize());
        page(page, userLambdaQueryWrapper);
        List<User> users = page.getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        PageVo<UserVo> userPageVo = new PageVo<>(page.getTotal(), userVos);
        return ResponseResult.okResult(userPageVo);
    }

    @Override
    public ResponseResult updateUser(User user)
    {
        User user1 = userMapper.selectById(user.getId());
        user1.setStatus(user.getStatus());
        userMapper.updateById(user1);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(Long id)
    {
        userMapper.deleteById(id);
        return ResponseResult.okResult();
    }
}




