package com.runner.remoting.netty.demo.server;

import com.runner.remoting.netty.demo.server.NettyServer;

import java.util.concurrent.CountDownLatch;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 13:57
 */
public class ServerMain {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        NettyServer nettyServer = new NettyServer(9990, true, 60);
        latch.await();
    }
}
