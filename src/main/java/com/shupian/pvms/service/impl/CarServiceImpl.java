package com.shupian.pvms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shupian.pvms.domain.entity.Car;
import com.shupian.pvms.service.CarService;
import com.shupian.pvms.mapper.CarMapper;
import org.springframework.stereotype.Service;

/**
* @author 19165
* @description 针对表【car】的数据库操作Service实现
* @createDate 2022-05-31 14:43:48
*/
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car>
    implements CarService{

}




