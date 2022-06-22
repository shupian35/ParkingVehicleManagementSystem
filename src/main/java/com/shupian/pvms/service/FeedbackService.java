package com.shupian.pvms.service;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Feedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shupian.pvms.domain.vo.FeedbackSearchVo;

/**
* @author 19165
* @description 针对表【feedback】的数据库操作Service
* @createDate 2022-05-31 14:43:48
*/
public interface FeedbackService extends IService<Feedback> {
    ResponseResult getFeedbackList(Integer pageNum, Integer pageSize, Integer tag);

    ResponseResult postResult(Long id, String status);

    ResponseResult search(FeedbackSearchVo feedbackSearchVo);

    ResponseResult updateFeedback(Feedback feedback);

    ResponseResult deleteFeedBack(Long id);
}
