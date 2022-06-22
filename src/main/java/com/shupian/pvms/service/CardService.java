package com.shupian.pvms.service;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Card;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 19165
* @description 针对表【card】的数据库操作Service
* @createDate 2022-05-31 14:43:48
*/
public interface CardService extends IService<Card> {

    ResponseResult getCardTagNums(String start, String end);
}
