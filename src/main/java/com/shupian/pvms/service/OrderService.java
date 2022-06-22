package com.shupian.pvms.service;

import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shupian.pvms.domain.vo.CarRegisterVo;

import java.util.Map;

/**
* @author 19165
* @description 针对表【pvms_order】的数据库操作Service
* @createDate 2022-06-01 16:45:22
*/
public interface OrderService extends IService<Order> {

    ResponseResult totalRevenue(String start, String end);

    ResponseResult intoPark(CarRegisterVo carRegisterVo);

    ResponseResult outPark(CarRegisterVo carRegisterVo);

    ResponseResult getRecentData(String Date);

    ResponseResult totalNum(String start, String end);

    ResponseResult payTagNums(String start, String end);

    ResponseResult getTimeLine();

    void fallback(Map<String, String> params);

    ResponseResult getCarsInfo();
}
