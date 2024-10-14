package com.runner.intercept.advice;

import net.bytebuddy.asm.Advice;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/30 13:54
 */
public class MyServiceAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Argument(0) String taskName) {
        System.out.println(Thread.currentThread().getName() + " Before executeTask logic.===> task name is :" + taskName);
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Return String returnValue) {
        System.out.println(Thread.currentThread().getName() + " After executeTask logic. ===> return value is :" + returnValue);
    }
}
