package com.shupian.pvms.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shupian.pvms.constants.TimeConstants;
import com.shupian.pvms.controller.OrderController;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.Car;
import com.shupian.pvms.domain.entity.Card;
import com.shupian.pvms.domain.entity.Order;
import com.shupian.pvms.domain.entity.User;
import com.shupian.pvms.domain.vo.CarRegisterVo;
import com.shupian.pvms.domain.vo.CarVo;
import com.shupian.pvms.domain.vo.TimeLine;
import com.shupian.pvms.exception.SystemException;
import com.shupian.pvms.mapper.CarMapper;
import com.shupian.pvms.mapper.CardMapper;
import com.shupian.pvms.mapper.UserMapper;
import com.shupian.pvms.service.CarService;
import com.shupian.pvms.service.OrderService;
import com.shupian.pvms.mapper.OrderMapper;
import com.shupian.pvms.service.UserService;
import com.shupian.pvms.utils.BeanCopyUtils;
import com.shupian.pvms.utils.MoneyUtil;
import com.shupian.pvms.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import static com.shupian.pvms.enums.AppHttpCodeEnum.*;

/**
 * @author 19165
 * @description 针对表【pvms_order】的数据库操作Service实现
 * @createDate 2022-06-01 16:45:22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService
{
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CarMapper carMapper;

    @Resource
    private CardMapper cardMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 一段时间内总收入
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public ResponseResult totalRevenue(String start, String end)
    {
        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Order> between = lambdaQueryWrapper.between(Order::getEndTime, start, end);
        List<Order> orders = orderMapper.selectList(between);
        BigDecimal reduce = orders.stream().map(order -> order.getAmount()).reduce(BigDecimal.valueOf(0), (bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2));

        Map<String, BigDecimal> map = new HashMap<>();
        map.put("totalRevenue", reduce);

        return ResponseResult.okResult(map);
    }

    /**
     * 入场登记
     *
     * @param carRegisterVo
     * @return
     */
    @Override
    public ResponseResult intoPark(CarRegisterVo carRegisterVo)
    {
        String carNumber = carRegisterVo.getCarNumber();
        String intoTime = carRegisterVo.getIntoTime();
        String parkingLot = carRegisterVo.getParkingLot();
        if (StringUtils.isAnyBlank(carNumber, intoTime, parkingLot))
        {
            throw new SystemException(PARAMETER_ERROR);
        }
        Order order = new Order();
        order.setCarNumber(carNumber);
        order.setStartTime(intoTime);
        order.setCarLocat(parkingLot);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getCarNumber, carNumber).eq(Order::getEndTime, "1999-12-12 00:00:00");
        if (Objects.nonNull(orderMapper.selectOne(queryWrapper)))
        {
            throw new SystemException(PARAMETER_ERROR, "已登记请勿重复操作");
        }
        //插入数据
        orderMapper.insert(order);
        //将车辆入场信息存入redis list
        TimeLine timeLine = new TimeLine(intoTime + " " + carNumber + " 入场", "green");
        redisCache.setCacheList("carTimeLine", Arrays.asList(timeLine));
        return ResponseResult.okResult();
    }

    /**
     * 出场登记
     *
     * @param carRegisterVo
     * @return
     */
    @Override
    public ResponseResult outPark(CarRegisterVo carRegisterVo)
    {
        String outTime = carRegisterVo.getOutTime();
        String carNumber = carRegisterVo.getCarNumber();
        //非空校验
        if (StringUtils.isAnyBlank(outTime, carNumber))
        {
            throw new SystemException(PARAMETER_ERROR);
        }

        DateTime dateTime = DateUtil.parse(outTime);
        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Order::getCarNumber, carNumber)
                .eq(Order::getEndTime, "1999-12-12 00:00:00");
        Order order = orderMapper.selectOne(lambdaQueryWrapper);
        if (order == null)
        {
            throw new SystemException(ORDER_OK, "已支付，请勿重复操作");
        }
        //将车辆入场信息存入redis list
        TimeLine timeLine = new TimeLine(outTime + " " + carNumber + " 出场", "red");
        redisCache.setCacheList("carTimeLine", Arrays.asList(timeLine));


        order.setEndTime(outTime);
        String startTime = order.getStartTime();
        Map<String, Object> map = new HashMap<>();
        // 计算停车时间
        long between = DateUtil.between(DateUtil.parse(startTime), dateTime, DateUnit.MINUTE);
        // 停车时间小于30分钟免费
        if (between < 30L)
        {
            map.put("free", "true");
            map.put("totalTime", between);
            order.setAmount(new BigDecimal(0));
            orderMapper.updateById(order);
            return ResponseResult.okResult(map);
        }
        else
        {
            orderMapper.updateById(order);
            //如果有为持卡用户则判断是否能使用卡支付
            String carNumber1 = order.getCarNumber();
            LambdaQueryWrapper<Car> carLambdaQueryWrapper = new LambdaQueryWrapper<>();
            carLambdaQueryWrapper.eq(Car::getCarNumber, carNumber1);
            Car car = carMapper.selectOne(carLambdaQueryWrapper);
            String tag = null;      //卡类型
            BigDecimal amount = new BigDecimal(0);      //余额
            //当为持卡用户获取卡类型
            if (car != null)
            {
                LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
                cardLambdaQueryWrapper.eq(Card::getId, car.getCardId());
                Card card = cardMapper.selectOne(cardLambdaQueryWrapper);
                amount = card.getAmount();
                tag = card.getTag();
            }

            String isVip;
            if (Objects.nonNull(tag))
            {
                //持卡用户
                BigDecimal discount = MoneyUtil.Money(between, tag);    //折扣费用
                map.put("discount", discount);
                if (amount.compareTo(discount) > -1)
                {
                    isVip = tag;
                }
                else
                {
                    isVip = "-2";      //卡余额不足
                }
            }
            else
            {
                //非持卡用户
                isVip = "-1";       //不能使用卡支付
            }
            BigDecimal money = MoneyUtil.Money(between);      //未折扣费用
            map.put("free", "false");
            map.put("isVip", isVip);
            map.put("money", money);
            map.put("totalTime", between);
            map.put("orderId", order.getId().toString());
            map.put("parkingLot", order.getCarLocat());
            return ResponseResult.okResult(map);
        }
    }

    /**
     * 获取最近一天中每个时间段的车流数据
     *
     * @return
     */
    @Override
    public ResponseResult getRecentData(String Date)
    {
        String[] hoursArray = {" 00:00:00", " 02:00:00", " 04:00:00", " 06:00:00", " 08:00:00", " 10:00:00", " 12:00:00", " 14:00:00", " 16:00:00", " 18:00:00", " 20:00:00", " 22:00:00", " 23:59:59"};
        List<Integer> nums = new ArrayList<>();
        List<Integer> nums2 = new ArrayList<>();
        Map<String, List<Integer>> map = new HashMap<>();
        String startTime;
        String endTime;
        for (int i = 0; i < hoursArray.length - 1; i++)
        {
            startTime = Date + hoursArray[i];
            endTime = Date + hoursArray[i + 1];
            LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderLambdaQueryWrapper.between(Objects.nonNull(Date), Order::getStartTime, startTime, endTime);
            Integer integer = orderMapper.selectCount(orderLambdaQueryWrapper);
            if (integer == null)
            {
                nums.add(0);
            }
            else
            {
                nums.add(integer);
            }
            LambdaQueryWrapper<Order> orderLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
            orderLambdaQueryWrapper2.between(Objects.nonNull(Date), Order::getEndTime, startTime, endTime);
            Integer integer1 = orderMapper.selectCount(orderLambdaQueryWrapper2);
            if (integer1 == null)
            {
                nums2.add(0);
            }
            else
            {
                nums2.add(integer);
            }
        }
        map.put("data1", nums);
        map.put("data2", nums2);
        return ResponseResult.okResult(map);
    }

    /**
     * 一段时间内总订单数
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public ResponseResult totalNum(String start, String end)
    {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLambdaQueryWrapper.between(Objects.nonNull(start) && Objects.nonNull(end), Order::getZfTime, start, end);
        Integer integer = orderMapper.selectCount(orderLambdaQueryWrapper);
        Map<String, Integer> map = new HashMap<>();
        if (integer == null)
        {
            map.put("total", 0);
        }
        else
        {
            map.put("total", integer);
        }
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult payTagNums(String start, String end)
    {
        String[] str = new String[]{"支付宝", "微信", "银联", "现金"};

        List<Map> mapList = new ArrayList<>();
        int[] list = new int[4];
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLambdaQueryWrapper.between(Objects.nonNull(start) && Objects.nonNull(end), Order::getZfTime, start, end);
        List<Order> orders = orderMapper.selectList(orderLambdaQueryWrapper);
        for (Order order :
                orders)
        {
            Integer paymentTypes = order.getPaymentTypes();
            list[paymentTypes]++;
        }

        for (int i = 0; i < 4; i++)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("name", str[i]);
            map.put("value", list[i]);
            mapList.add(map);
        }


        return ResponseResult.okResult(mapList);
    }

    @Override
    public ResponseResult getTimeLine()
    {
        List<Object> carTimeLine = redisCache.getCacheList("carTimeLine");
        int size = carTimeLine.size();
        if (size > 3)
        {
            return ResponseResult.okResult(carTimeLine.subList(size - 4, size));
        }
        return ResponseResult.okResult(carTimeLine);
    }

    @Override
    public void fallback(Map<String, String> params)
    {

        Long out_trade_no = Long.valueOf(params.get("out_trade_no"));
        Order order = orderMapper.selectById(out_trade_no);
        order.setAmount(new BigDecimal(params.get("total_amount")));
        order.setZfTime(params.get("gmt_payment"));
        order.setPaymentTypes(0);
        logger.info(order.toString());
        orderMapper.updateById(order);

    }

    @Override
    public ResponseResult getCarsInfo()
    {

        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLambdaQueryWrapper.eq(Order::getEndTime, "1999-12-12 00:00:00");
        List<Order> orders = orderMapper.selectList(orderLambdaQueryWrapper);
        for (Order order : orders)
        {
            //根据车牌号查询卡号
            String carNumber = order.getCarNumber();
            LambdaQueryWrapper<Car> carLambdaQueryWrapper = new LambdaQueryWrapper<>();
            carLambdaQueryWrapper.eq(Car::getCarNumber, carNumber);
            Car car = carMapper.selectOne(carLambdaQueryWrapper);
            if (Objects.isNull(car))
            {
                order.setUsername("未注册");
                break;
            }
            //根据卡号查询用户id
            Long cardId = car.getCardId();
            LambdaQueryWrapper<Card> cardLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cardLambdaQueryWrapper.eq(Card::getId, cardId);
            Card card = cardMapper.selectOne(cardLambdaQueryWrapper);
            if (Objects.isNull(card))
            {
                order.setUsername("未注册");
                break;
            }
            //根据用户id查询用户姓名
            Long userId = card.getUserId();
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, userId);
            User user = userMapper.selectOne(userLambdaQueryWrapper);
            if (Objects.isNull(user))
            {
                order.setUsername("未注册");
                break;
            }
            String username = user.getUsername();
            if(Objects.isNull(username)){
                order.setUsername("未注册");
                break;
            }
            order.setUsername(username);

        }
        List<CarVo> carVos = BeanCopyUtils.copyBeanList(orders, CarVo.class);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("stopNew", carVos);
        return ResponseResult.okResult(map);
    }
}




