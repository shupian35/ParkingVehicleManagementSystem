package com.shupian.pvms.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 */
@Data
public class AdminLoginVo implements Serializable
{

    private static final long serialVersionUID = -4835709278962674666L;

    /** 账号 */
    private String username;

    /** 密码 */
    private String password;

    /** 验证码 */
    private String captcha;

    /** UUID */
    private String uuid;

}
