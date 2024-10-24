package com.runner.remoting;

import java.util.concurrent.ExecutorService;

public interface RemotingServer extends RemotingService{


//    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    int localListenPort();

   /* Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    Pair<NettyRequestProcessor, ExecutorService> getDefaultProcessorPair();

    RemotingServer newRemotingServer(int port);

    void removeRemotingServer(int port);

    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;

    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;*/
}
