package com.shupian.pvms.controller;

import com.shupian.pvms.annotation.CheckToken;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.service.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "会员卡相关接口")
@RequestMapping("/card")
@RestController
public class CardController {

    @Resource
    private CardService cardService;

    @ApiOperation("指定时间范围内的卡数据")
    @GetMapping("/getCardTagNums/{start}/{end}")
    @CheckToken
    public ResponseResult getCardTagNums(@PathVariable String start,@PathVariable String end)
    {
        return cardService.getCardTagNums(start,end);
    }
}
