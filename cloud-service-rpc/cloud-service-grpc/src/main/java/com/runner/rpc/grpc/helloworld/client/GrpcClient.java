package com.runner.rpc.grpc.helloworld.client;

import com.runner.rpc.grpc.helloworld.generated.GreeterGrpc;
import com.runner.rpc.grpc.helloworld.generated.HelloReply;
import com.runner.rpc.grpc.helloworld.generated.HelloRequest;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

/**
 * client side build by grpc
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/13 13:25
 */
public class GrpcClient {

    public static void main( String[] args ) throws Exception {
        // Channel is the abstraction to connect to a service endpoint
        // Let's use plaintext communication because we don't have certs
        final ManagedChannel channel = Grpc.newChannelBuilder("localhost:8888",  InsecureChannelCredentials.create()).build();
/*        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8888")
                .usePlaintext()
                .build();*/

        // It is up to the client to determine whether to block the call
        // Here we create a blocking stub, but an async stub,
        // or an async stub with Future are always possible.
        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
        HelloRequest request = HelloRequest.newBuilder()
                        .setName("Tom and Jerry")
                        .build();

        // Finally, make the call using the stub
        HelloReply response =
                stub.sayHello(request);


        System.out.println("client receive response message is " + response.getMessage());

        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();
    }
}
