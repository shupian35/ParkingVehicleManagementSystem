package com.shupian.pvms.domain.vo;

import lombok.Data;

/**
 * This class is for xxxx.
 *
 * @author wzy
 * @version 2022/6/12 19:18
 */
@Data
public class CarVo {
    /**
     * 用户名
     */
    private String username;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 车位
     */
    private String carLocat;
}
