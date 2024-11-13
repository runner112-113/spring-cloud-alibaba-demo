package com.runner.remoting.netty.demo.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;

/**
 * have ability of Both {@link ChannelOutboundHandler} and {@link ChannelInboundHandler}
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 10:53
 */
public class ServerChannelHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server --> receive message is :" + msg);
        super.channelRead(ctx, msg);
        ctx.channel().writeAndFlush("I have receive message, THX");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("exception is :" + cause.getMessage());
    }
}
