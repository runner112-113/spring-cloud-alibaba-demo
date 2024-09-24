package com.runner.service.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author Runner
 * @version 1.0
 * @date 2024/6/28 14:58
 * @description
 */
@Activate(group = CommonConstants.PROVIDER)
public class MyFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        System.out.println("====before invoke =====");
        Result result = invoker.invoke(invocation);
        System.out.println("====after invoke =====");
        return result;
    }
}
