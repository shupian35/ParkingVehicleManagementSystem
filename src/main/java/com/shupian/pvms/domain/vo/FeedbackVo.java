package com.shupian.pvms.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class FeedbackVo {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 联系方式
     */
    private String phonenumber;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 处理类型
     */
    private String tag;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 状态(0 未处理 1 处理中 2 已处理 ）
     */
    private String status;

}
