package com.eastmoney.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/19 13:21
 */

@Aspect
public class MyAspect {


    public static MyAspect aspectOf(){
        return new MyAspect();
    }

    @Pointcut("execution(public * com.eastmoney.aspectj.MyService.*())")
    private void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("before ....");
        Object proceed = joinPoint.proceed();
        System.out.println("after ....");

        return proceed;

    }


}
