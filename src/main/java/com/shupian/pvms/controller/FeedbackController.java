package com.shupian.pvms.controller;

import com.shupian.pvms.annotation.CheckToken;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Feedback;
import com.shupian.pvms.domain.vo.FeedbackSearchVo;
import com.shupian.pvms.service.FeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "反馈相关接口")
@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    /**
     * 获取反馈问题列表
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param tag      问题状态
     * @return
     */
    @ApiOperation("获取反馈问题列表")
    @GetMapping("/feedbackList/{pageNum}/{pageSize}")
    @CheckToken
    public ResponseResult getFeedbackList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, Integer tag)
    {
        return feedbackService.getFeedbackList(pageNum, pageSize, tag);
    }

    /**
     * 修改反馈状态
     *
     * @param id     问题id
     * @param status 问题状态
     * @return
     */
    @ApiOperation("修改反馈状态")
    @GetMapping("/postResult")
    @CheckToken
    public ResponseResult postResult(Long id, String status)
    {
        return feedbackService.postResult(id, status);
    }


    @ApiOperation("根据条件搜索")
    @PostMapping("/search")
    @CheckToken
    public ResponseResult search(@RequestBody FeedbackSearchVo feedbackSearchVo)
    {
        return feedbackService.search(feedbackSearchVo);
    }

    @ApiOperation("更新反馈信息")
    @PutMapping("/update")
    @CheckToken
    public ResponseResult updateFeedback(@RequestBody Feedback feedback)
    {
        return feedbackService.updateFeedback(feedback);
    }

    @ApiOperation("根据id删除")
    @DeleteMapping("/delete/{id}")
    @CheckToken
    public ResponseResult deleteFeedBack(@PathVariable Long id)
    {
        return feedbackService.deleteFeedBack(id);
    }


}
