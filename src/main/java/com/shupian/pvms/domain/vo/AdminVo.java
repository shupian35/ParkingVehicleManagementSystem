package com.shupian.pvms.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * This class is for xxxx.
 *
 * @author wzy
 * @version 2022/6/9 22:40
 */
@Data
public class AdminVo
{
    /**
     * id
     */
    private Long id;

    /**
     * 管理员名称
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private Date createTime;
}
