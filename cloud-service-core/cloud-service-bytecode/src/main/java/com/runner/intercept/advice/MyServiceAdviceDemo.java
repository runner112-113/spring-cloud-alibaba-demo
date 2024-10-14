package com.runner.intercept.advice;

import com.runner.intercept.api.MyService;
import com.runner.intercept.methoddelegation.MyServiceInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/30 13:55
 */
public class MyServiceAdviceDemo {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        // 使用 ByteBuddy 动态增强 MyService 类
        MyService myService = new ByteBuddy()
                .subclass(MyService.class) // 基于 MyService 类创建子类
                .method(ElementMatchers.named("executeTask")) // 匹配 executeTask 方法
                .intercept(Advice.to(MyServiceAdvice.class)) // 使用拦截器
                .make() // 创建动态类
                .load(MyService.class.getClassLoader()) // 加载动态类
                .getLoaded()
                .newInstance(); // 创建增强类的实例

        // 调用被拦截的方法
        String result = myService.executeTask("task1");
        System.out.println(Thread.currentThread().getName() + " Final result: " + result);
    }
}
