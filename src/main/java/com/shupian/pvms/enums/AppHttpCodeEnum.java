package com.shupian.pvms.enums;

/**
 * 自定义状态
 */
public enum AppHttpCodeEnum
{

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 需要登录后操作
     */
    NEED_LOGIN(401, "需要登录后操作"),

    /**
     * 无权限操作
     */
    NO_OPERATOR_AUTH(403, "无权限操作"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),

    /**
     * 请求参数错误
     */
    PARAMETER_ERROR(400, "请求参数错误"),

    /**
     * 账号已存在
     */
    USERNAME_EXIST(501, "账号已存在"),

    /**
     * 手机号已存在
     */
    PHONE_NUMBER_EXIST(502, "手机号已存在"),

    /**
     * 邮箱已存在
     */
    EMAIL_EXIST(503, "邮箱已存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(505, "账号或密码错误"),

    /**
     * 订单已存在
     */
    ORDER_OK(506,"订单已存在");

    /**
     * 状态码
     */
    int code;

    /**
     * 信息提示
     */
    String msg;

    AppHttpCodeEnum(int code, String errorMessage)
    {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode()
    {
        return code;
    }

    public String getMsg()
    {
        return msg;
    }
}
