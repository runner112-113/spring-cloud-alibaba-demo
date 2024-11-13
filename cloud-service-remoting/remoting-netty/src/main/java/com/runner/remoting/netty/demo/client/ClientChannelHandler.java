package com.runner.remoting.netty.demo.client;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * have ability of Both {@link ChannelOutboundHandler} and {@link ChannelInboundHandler}
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 10:53
 */
public class ClientChannelHandler extends ChannelDuplexHandler {

    private static final Map<InetSocketAddress, Channel> channelMap = new ConcurrentHashMap<>();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client ---> receive message is : " + msg);
        super.channelRead(ctx, msg);
    }

/*    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
        ctx.channel().writeAndFlush("Hello Netty Server");
    }*/

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelMap.putIfAbsent((InetSocketAddress)ctx.channel().remoteAddress(), ctx.channel());
        ctx.channel().writeAndFlush("Hello Netty Server");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelMap.remove((InetSocketAddress)ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    public static Channel findChannel(InetSocketAddress address) {
        return channelMap.get(address);
    }
}
