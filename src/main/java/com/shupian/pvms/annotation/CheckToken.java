package com.shupian.pvms.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD) // 说明该注解只能放在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckToken
{
}