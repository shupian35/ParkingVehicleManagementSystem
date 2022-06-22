package com.shupian.pvms.aspect;


import com.shupian.pvms.exception.SystemException;
import com.shupian.pvms.utils.RedisCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.shupian.pvms.enums.AppHttpCodeEnum.NEED_LOGIN;

/**
 * 接口请求限制切面类
 *
 * @author wzy
 * @version 2022/5/26 10:05
 */
@Aspect
@Component
public class CheckTokenAspect
{

    @Autowired
    private RedisCache redisCache;

    // 定义切点
    // 让所有有@CheckToken注解的方法都执行切面方法
    @Pointcut("@annotation(com.shupian.pvms.annotation.CheckToken)")
    public void pt()
    {
    }

    @Before(value = "pt()")
    public Object doAround() throws Throwable
    {

        // 获得request对象
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = Objects.requireNonNull(sra).getRequest();

        String token = request.getHeader("token");
        if (Objects.nonNull(token))
        {

//            Object adminId1 = request.getSession().getAttribute("adminId");
            String adminId = request.getHeader("adminId");
            if (Objects.isNull(adminId))
            {
                throw new SystemException(NEED_LOGIN);
            }
            Object cacheObject = redisCache.getCacheObject("token" + adminId);
            if (Objects.isNull(cacheObject) || !token.equals(cacheObject.toString()))
            {
                throw new SystemException(NEED_LOGIN);
            }
            else
            {
                return true;
            }
        }
        throw new SystemException(NEED_LOGIN);
    }

}