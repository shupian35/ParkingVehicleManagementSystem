package com.shupian.pvms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PageVo <T>{
    /**
     * 总条数
     */
    private Long total;
    /**
     * 数据
     */
    private List<T> data;

}
