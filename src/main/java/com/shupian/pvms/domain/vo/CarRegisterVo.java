package com.shupian.pvms.domain.vo;

import lombok.Data;

/**
 * This class is for xxxx.
 *
 * @author wzy
 * @version 2022/6/10 16:09
 */
@Data
public class CarRegisterVo
{

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 入场时间
     */
    private String intoTime;

    /**
     * 出场时间
     */
    private String outTime;

    /**
     * 车位
     */
    private String parkingLot;
}
