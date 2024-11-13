package com.runner.remoting.netty.demo.server;

import com.runner.remoting.netty.NettyEventLoopFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Netty Server
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 10:56
 */
public class NettyServer {

    Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    public NettyServer(int port, boolean keepalive, long closeTimeout) {
        initServerBootstrap(keepalive, closeTimeout);
        bind(port);
    }

    public void initServerBootstrap(boolean keepalive, long closeTimeout) {
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "netty-boss-thread");
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Runtime.getRuntime().availableProcessors() + 1 , "netty-worker-thread");
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, keepalive)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast("negotiation", new SslServerTlsHandler(getUrl()));
                        ch.pipeline()
                                .addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, closeTimeout, MILLISECONDS))
                                .addLast("server-handler", createServerHandler());
                    }
                });

    }

    public void bind(int port) {
        try {
            ChannelFuture channelFuture = bootstrap.bind(port);
            channelFuture.syncUninterruptibly();
            channel = channelFuture.channel();
        }catch (Throwable e) {
            closeBootstrap();
            throw  e;
        }
    }

    private ChannelHandler createServerHandler() {
        // todo wrapper? build the chain of responsibility？
        return new ServerChannelHandler();
    }

    private void closeBootstrap() {
        try {
            if (bootstrap != null) {
//                long timeout = ConfigurationUtils.reCalShutdownTime(serverShutdownTimeoutMills);
//                long quietPeriod = Math.min(2000L, timeout);
                long quietPeriod = 2000;
                long timeout = 2000;
                Future<?> bossGroupShutdownFuture = bossGroup.shutdownGracefully(quietPeriod, timeout, MILLISECONDS);
                Future<?> workerGroupShutdownFuture =
                        workerGroup.shutdownGracefully(quietPeriod, timeout, MILLISECONDS);
                bossGroupShutdownFuture.syncUninterruptibly();
                workerGroupShutdownFuture.syncUninterruptibly();
            }
        } catch (Throwable e) {
            logger.warn("closeBootstrap error!", e);
        }
    }
}
