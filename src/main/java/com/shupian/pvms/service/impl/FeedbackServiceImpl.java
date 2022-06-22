package com.shupian.pvms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Feedback;
import com.shupian.pvms.domain.entity.User;
import com.shupian.pvms.domain.vo.FeedbackSearchVo;
import com.shupian.pvms.domain.vo.FeedbackVo;
import com.shupian.pvms.domain.vo.PageVo;
import com.shupian.pvms.enums.AppHttpCodeEnum;
import com.shupian.pvms.mapper.FeedbackMapper;
import com.shupian.pvms.mapper.UserMapper;
import com.shupian.pvms.service.FeedbackService;
import com.shupian.pvms.utils.BeanCopyUtils;
import com.shupian.pvms.utils.FeedbackUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author 19165
 * @description 针对表【feedback】的数据库操作Service实现
 * @createDate 2022-05-31 14:43:48
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback>
        implements FeedbackService
{
    @Resource
    private FeedbackMapper feedbackMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * @param pageNum
     * @param pageSize
     * @param tag
     * @return
     */
    @Override
    public ResponseResult getFeedbackList(Integer pageNum, Integer pageSize, Integer tag)
    {

        //查询添加
        LambdaQueryWrapper<Feedback> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果有categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(tag) && tag > 0, Feedback::getTag, tag);

        lambdaQueryWrapper.orderByDesc(Feedback::getCreateTime);
        //分页查询
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);
        long total = page.getTotal();
        List<Feedback> records = page.getRecords();
        for (Feedback feedback : records)
        {
            Long userId = feedback.getUserId();
            User user = userMapper.selectById(userId);
            feedback.setUsername(user.getUsername());
            feedback.setPhonenumber(user.getPhonenumber());
        }
        List<FeedbackVo> feedbackVos = BeanCopyUtils.copyBeanList(records, FeedbackVo.class);
        PageVo<FeedbackVo> objectPageVo = new PageVo<>(total, feedbackVos);
        return ResponseResult.okResult(objectPageVo);

    }

    @Override
    public ResponseResult postResult(Long id, String status)
    {
        Feedback feedback = feedbackMapper.selectById(id);
        feedback.setStatus(status);
        int i = feedbackMapper.updateById(feedback);
        if (i == 1)
            return ResponseResult.okResult();
        else
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public ResponseResult search(FeedbackSearchVo feedbackSearchVo)
    {
        User user = null;
        String status = feedbackSearchVo.getStatus();
        List<String> tag = feedbackSearchVo.getTag();
        String endDate = feedbackSearchVo.getEndDate();
        String startDate = feedbackSearchVo.getStartDate();
        String username = feedbackSearchVo.getUsername();
        LambdaQueryWrapper<Feedback> feedbackLambdaQueryWrapper = new LambdaQueryWrapper<>();
        feedbackLambdaQueryWrapper.eq(status != null && status.length() > 0, Feedback::getStatus, feedbackSearchVo.getStatus());
        feedbackLambdaQueryWrapper.between(Objects.nonNull(endDate) && Objects.nonNull(startDate) && endDate.length() > 0 && startDate.length() > 0, Feedback::getCreateTime, feedbackSearchVo.getStartDate(), feedbackSearchVo.getEndDate());
        if (!tag.isEmpty())
        {
            String sql ="(tag = ";
            for (int i = 0;i<tag.size()-1;i++)
            {
                sql+="\""+tag.get(i)+"\""+" OR tag = ";
            }
            sql += "\""+tag.get(tag.size()-1)+"\")";
            feedbackLambdaQueryWrapper.apply(sql);
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(username) && username.length() > 0)
        {
            userLambdaQueryWrapper.like(User::getUsername, feedbackSearchVo.getUsername());
            user = userMapper.selectOne(userLambdaQueryWrapper);
        }


        if (user == null)
        {
            Page<Feedback> page1 = new Page<>(feedbackSearchVo.getPageNum(), feedbackSearchVo.getPageSize());
            page(page1, feedbackLambdaQueryWrapper);

            List<Feedback> records = page1.getRecords();

            for (Feedback feedback : records)
            {
                Long userId = feedback.getUserId();
                User user1 = userMapper.selectById(userId);
                feedback.setUsername(user1.getUsername());
                feedback.setPhonenumber(user1.getPhonenumber());
            }
            List<FeedbackVo> feedbackVos = BeanCopyUtils.copyBeanList(records, FeedbackVo.class);
            PageVo<FeedbackVo> feedbackVoPageVo = new PageVo<FeedbackVo>(page1.getTotal(), feedbackVos);
            return ResponseResult.okResult(feedbackVoPageVo);
        }
        feedbackLambdaQueryWrapper.eq(Feedback::getUserId, user.getId());
        Page<Feedback> page = new Page<>(feedbackSearchVo.getPageNum(), feedbackSearchVo.getPageSize());
        page(page, feedbackLambdaQueryWrapper);
        List<Feedback> records = page.getRecords();
        long total = page.getTotal();
        for (Feedback feedback : records)
        {
            Long userId = feedback.getUserId();
            User user1 = userMapper.selectById(userId);
            feedback.setUsername(user1.getUsername());
            feedback.setPhonenumber(user1.getPhonenumber());
        }
        List<FeedbackVo> feedbackVos = BeanCopyUtils.copyBeanList(records, FeedbackVo.class);
        PageVo<FeedbackVo> objectPageVo = new PageVo<>(total, feedbackVos);
        return ResponseResult.okResult(objectPageVo);
    }

    @Override
    public ResponseResult updateFeedback(Feedback feedback)
    {
        Feedback feedback1 = feedbackMapper.selectById(feedback.getId());
        feedback1.setStatus(feedback.getStatus());
        feedbackMapper.updateById(feedback1);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteFeedBack(Long id)
    {
        feedbackMapper.deleteById(id);
        return ResponseResult.okResult();
    }
}




