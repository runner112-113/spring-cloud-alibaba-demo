package com.runner.rpc.grpc.helloworld.server;

import com.runner.rpc.grpc.helloworld.GreeterImpl;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

/**
 * server side build by grpc
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/13 13:21
 */
public class GrpcServer {


    public static void main(String[] args) throws Exception {
        // Create a new server to listen on port 8080

        Server  server = Grpc.newServerBuilderForPort( 8888, InsecureServerCredentials.create())
                .addService(new GreeterImpl())
                .build()
                .start();
/*        Server server = ServerBuilder.forPort(8888)
                .addService(new GreetingImpl())
                .build();*/

        // Start the server

        // Server threads are running in the background.
        System.out.println("Server started, listen port 8888");
        // Don't exit the main thread. Wait until server is terminated.
        server.awaitTermination();

    }
}
