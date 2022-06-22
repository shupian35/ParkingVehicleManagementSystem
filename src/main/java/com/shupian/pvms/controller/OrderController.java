package com.shupian.pvms.controller;

import com.alipay.api.AlipayApiException;
import com.shupian.pvms.annotation.CheckToken;
import com.shupian.pvms.constants.SystemConstants;
import com.shupian.pvms.domain.ResponseResult;
import com.shupian.pvms.domain.entity.AlipayBean;
import com.shupian.pvms.domain.vo.CarRegisterVo;
import com.shupian.pvms.service.OrderService;
import com.shupian.pvms.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "订单相关接口")
@RestController
@RequestMapping("/order")
public class OrderController
{

    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;


    @ApiOperation("一段时间内总收入")
    @GetMapping("/totalRevenue/{start}/{end}")
    @CheckToken
    public ResponseResult totalRevenue(@PathVariable String start, @PathVariable String end)
    {
        return orderService.totalRevenue(start, end);
    }

    @ApiOperation("汽车进停车场")
    @PostMapping("/intoPark")
    @CheckToken
    public ResponseResult intoPark(@RequestBody CarRegisterVo carRegisterVo)
    {
        logger.info("汽车进停车场");
        return orderService.intoPark(carRegisterVo);
    }

    /**
     * 出场登记
     *
     * @param carRegisterVo
     * @return
     */
    @ApiOperation("汽车出停车场")
    @PostMapping("/outPark")
    @CheckToken
    public ResponseResult outPark(@RequestBody CarRegisterVo carRegisterVo)
    {
        logger.info("汽车出停车场");
        return orderService.outPark(carRegisterVo);
    }

    @ApiOperation("支付接口")
    @GetMapping("/pay/{outTradeNo}/{totalAmount}")
    @CheckToken
    public ResponseResult<String> alipay(@PathVariable String outTradeNo, @PathVariable String totalAmount) throws AlipayApiException
    {

        logger.info("商户订单号为{},订单名称为{},付款金额为{},商品描述{}", outTradeNo, SystemConstants.SUBJECT, totalAmount, SystemConstants.BODY);
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(SystemConstants.SUBJECT);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(SystemConstants.BODY);

        return ResponseResult.okResult(payService.aliPay(alipayBean));
    }

    /**
     *  支付成功的回调
     * */
    @ApiOperation("支付宝回调接口")
    @PostMapping(value = "/fallback")
    public String fallback (HttpServletRequest request) throws Exception
    {

        if (request.getParameter("trade_status").equals("TRADE_SUCCESS"))
        {
            logger.info("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet())
            {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }
            logger.info(params.toString());
            orderService.fallback(params);
            return "success";
        }

        return "error";
    }

    /**
     * 一天中每个时间段的车流数据
     *
     * @return
     */
    @ApiOperation("获取最近一天中每个时间段的车流数据")
    @GetMapping("/getCarByDate/{Date}")
    @CheckToken
    public ResponseResult getRecentData(@PathVariable("Date") String Date)
    {
        return orderService.getRecentData(Date);
    }

    @ApiOperation("一段时间内总订单数")
    @GetMapping("/totalNum/{start}/{end}")
    @CheckToken
    public ResponseResult totalNum(@PathVariable String start, @PathVariable String end)
    {
        return orderService.totalNum(start, end);
    }

    @ApiOperation("一段时间内订单各类支付渠道")
    @GetMapping("/payTagNums/{start}/{end}")
    @CheckToken
    public ResponseResult payTagNums(@PathVariable String start, @PathVariable String end)
    {
        return orderService.payTagNums(start, end);
    }

    @ApiOperation("获取实时车辆出入信息")
    @GetMapping("/timeLine")
    @CheckToken
    public ResponseResult getTimeLine()
    {
        return orderService.getTimeLine();
    }

    @ApiOperation("获取unity需要的信息")
    @GetMapping("/getCarsInfo")
    public ResponseResult getCarsInfo(){
        return orderService.getCarsInfo();
    }

}
