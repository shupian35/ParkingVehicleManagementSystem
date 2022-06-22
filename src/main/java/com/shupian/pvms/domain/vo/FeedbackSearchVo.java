package com.shupian.pvms.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class FeedbackSearchVo {

    private String username;
    /**
     * 处理类型
     */
    private List<String> tag;
    /**
     * 状态(0 未处理 1 处理中 2 已处理 ）
     */
    private String status;

    private String startDate;

    private String endDate;

    private Integer pageNum;

    private Integer pageSize;
}
