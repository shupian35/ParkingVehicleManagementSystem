package com.shupian.pvms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class is for xxxx.
 *
 * @author wzy
 * @version 2022/6/11 15:26
 */
@Data
@AllArgsConstructor
public class TimeLine
{
    /**
     * 描述信息
     */
    private String CarMessage;

    /**
     * 颜色
     */
    private String color;
}
