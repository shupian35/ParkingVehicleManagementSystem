package com.shupian.pvms.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shupian.pvms.constants.SystemConstants;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Card;
import com.shupian.pvms.enums.AppHttpCodeEnum;
import com.shupian.pvms.service.CardService;
import com.shupian.pvms.mapper.CardMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
* @author 19165
* @description 针对表【card】的数据库操作Service实现
* @createDate 2022-05-31 14:43:48
*/
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card>
    implements CardService{
    @Resource
    private CardMapper cardMapper;

    @Override
    public ResponseResult getCardTagNums(String start, String end) {
        DateTime startTime = DateUtil.parse(start);
        DateTime endTime = DateUtil.parse(end);
        long between = DateUtil.between(startTime,endTime,DateUnit.DAY);
        if (between<1){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        List<Integer> num1 = new ArrayList<>();
        List<Integer> num2 = new ArrayList<>();
        List<Integer> num3 = new ArrayList<>();

        DateTime tmp;
        tmp=DateUtil.offsetDay(startTime,1);
        do{
            int monthCard = 0, seasonCard = 0, yearCard = 0;
            LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cardLambdaQueryWrapper.le(Card::getCreateTime, tmp);
            List<Card> cards = cardMapper.selectList(cardLambdaQueryWrapper);

            if (cards==null){
                num1.add(monthCard);
                num2.add(seasonCard);
                num3.add(yearCard);
                break;
            }
            for (Card card: cards) {
                if (card.getTag().equals(SystemConstants.MONTH)){
                    monthCard++;
                }
                if (card.getTag().equals(SystemConstants.QUARTER)){
                    seasonCard++;
                }
                if (card.getTag().equals(SystemConstants.YEAR)){
                    yearCard++;
                }
            }
            num1.add(monthCard);
            num2.add(seasonCard);
            num3.add(yearCard);
            tmp=DateUtil.offsetDay(tmp,1);
        }while(!tmp.toString().equals(DateUtil.offsetDay(endTime,1).toString()));
        List<List<Integer>> nums = new ArrayList<>();
        nums.add(num1);
        nums.add(num2);
        nums.add(num3);
        return ResponseResult.okResult(nums);
    }
}




