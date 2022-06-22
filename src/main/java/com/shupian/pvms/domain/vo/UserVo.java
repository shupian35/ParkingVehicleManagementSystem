package com.shupian.pvms.domain.vo;

import lombok.Data;
@Data
public class UserVo {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;



    /**
     * 昵称
     */
    private String nickName;

    /**
     * 卡号
     */
    private String cardId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phonenumber;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

    /**
     * 创建时间
     */
    private String createTime;

}
