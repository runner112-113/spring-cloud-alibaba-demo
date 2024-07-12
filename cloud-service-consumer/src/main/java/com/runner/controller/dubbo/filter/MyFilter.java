package com.runner.controller.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @date 2024/7/12 10:26
 */
@Activate(group = CommonConstants.CONSUMER)
public class MyFilter implements Filter {
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        System.out.println("====before filter invoke =====");
        Result result = invoker.invoke(invocation);
        System.out.println("====after filter invoke =====");
        return result;
    }
}
