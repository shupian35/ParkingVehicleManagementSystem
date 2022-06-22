package com.shupian.pvms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName feedback
 */
@TableName(value ="feedback")
@Data
public class Feedback implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

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

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private String delFlag;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 联系方式
     */
    @TableField(exist = false)
    private String phonenumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}