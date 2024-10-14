package com.runner.intercept.methoddelegation;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/29 19:39
 */
public class MyServiceInterceptor {
/*    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperMethod Method method) throws Throwable {
        // 方法调用前
        System.out.println("Before method: " + method.getName());

        // 修改参数，比如将第一个参数转换为大写
        allArguments[0] = ((String) allArguments[0]).toUpperCase();

        // 返回结果，可以修改结果
        return allArguments[0]  + " - Intercepted";
    }*/

    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperMethod  Method superMethod,@Origin Method method) throws Throwable {
        // 方法调用前
        System.out.println("Before method: " + method.getName());

        // 修改参数，比如将第一个参数转换为大写
        allArguments[0] = ((String) allArguments[0]).toUpperCase();

        // 反射调用原方法
        Object result = superMethod.invoke(obj, allArguments);

        // 方法调用后
        System.out.println("After method: " + method.getName());

        // 返回结果，可以修改结果
        return result + " - Intercepted";
    }

}
