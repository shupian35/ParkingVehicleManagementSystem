package com.shupian.pvms.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Feedback;
import com.shupian.pvms.domain.entity.User;
import com.shupian.pvms.domain.vo.FeedbackVo;
import com.shupian.pvms.mapper.FeedbackMapper;
import com.shupian.pvms.mapper.UserMapper;

import java.util.List;

public class FeedbackUtils {

    public static ResponseResult getResponseResult(LambdaQueryWrapper<Feedback> feedbackLambdaQueryWrapper, FeedbackMapper feedbackMapper, UserMapper userMapper) {
        List<Feedback> feedbacks = feedbackMapper.selectList(feedbackLambdaQueryWrapper);
        for (Feedback feedback : feedbacks) {
            Long userId = feedback.getUserId();
            User user = userMapper.selectById(userId);
            feedback.setUsername(user.getUsername());
            feedback.setPhonenumber(user.getPhonenumber());
        }
        List<FeedbackVo> feedbackVos = BeanCopyUtils.copyBeanList(feedbacks, FeedbackVo.class);
        return ResponseResult.okResult(feedbackVos);
    }
}
