package com.runner.config;


import org.aspectj.lang.Aspects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

    @Aspect
    public  class MyAspect {



        public static MyAspect aspectOf() {
            return new MyAspect();
        }

        @Pointcut("execution(public * com.runner.controller.dubbo.DubboDemoController.sayHello())")
        public void pointcut(){
        }

        @Around("pointcut()")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

            System.out.println("start ----> " + System.currentTimeMillis());
            Object proceed = joinPoint.proceed();
            System.out.println("end ----> " + System.currentTimeMillis());

            return proceed;
        }
    }
