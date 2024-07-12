package com.runner.controller.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionScope;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.filter.ClusterFilter;

/**
 * @author Runner
 * @version 1.0
 * @date 2024/6/28 14:58
 * @description
 */
@Activate(group = CommonConstants.CONSUMER)
public class MyClusterFilter implements ClusterFilter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        System.out.println("====before clusterFilter invoke =====");
        Result result = invoker.invoke(invocation);
        System.out.println("====after clusterFilter invoke =====");
        return result;
    }
}
