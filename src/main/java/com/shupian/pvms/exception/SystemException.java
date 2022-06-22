package com.shupian.pvms.exception;

import com.shupian.pvms.enums.AppHttpCodeEnum;

/**
 * 自定义异常类
 */
public class SystemException extends RuntimeException
{
    private static final long serialVersionUID = -5926292653065284660L;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 信息提示
     */
    private String msg;
    /**
     * 详细描述
     */
    private String description;

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getCode()
    {
        return code;
    }

    public String getMsg()
    {
        return msg;
    }

    public String getDescription()
    {
        return description;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum)
    {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum, String description)
    {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
        this.description = description;
    }

}