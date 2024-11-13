package com.runner.remoting.netty.demo.client;

import java.util.concurrent.CountDownLatch;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/12 14:08
 */
public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9990);
        latch.await();
    }
}
