package com.runner.remoting.netty.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Netty Client
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 10:17
 */
public class NettyClient {

    private Bootstrap bootstrap;

    private EventLoopGroup workerGroup;


    Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public NettyClient(String host, int port) {
        bootstrap = initBootStrap( 60);
        connect(host, port);
    }

    public Bootstrap initBootStrap(long heartbeatInterval) {
        workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast("decoder", new StringDecoder(StandardCharsets.UTF_8));
                channel.pipeline().addLast("encoder", new StringEncoder(StandardCharsets.UTF_8));
                channel.pipeline().addLast("idle-state-handler", new IdleStateHandler(heartbeatInterval, 0, 0, MILLISECONDS));
                channel.pipeline().addLast("client-handler",createClientHandler());
            }
        });

        return bootstrap;
    }

    public void connect(String host, int port) {

        try {
            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.sync();
            // Wait until the connection is closed.
            channelFuture.channel().closeFuture().sync();
        }catch (Exception ex) {
            logger.error("Netty Client build error!", ex);
        }finally {
            workerGroup.shutdownGracefully();
        }
    }

    public ChannelHandler createClientHandler() {
        // TODO wrapper?
        return new ClientChannelHandler();
    }
}
