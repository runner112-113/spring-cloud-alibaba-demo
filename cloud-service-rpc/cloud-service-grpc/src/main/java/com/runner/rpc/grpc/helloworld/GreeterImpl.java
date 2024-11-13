package com.runner.rpc.grpc.helloworld;

import com.runner.rpc.grpc.helloworld.generated.GreeterGrpc;
import com.runner.rpc.grpc.helloworld.generated.HelloReply;
import com.runner.rpc.grpc.helloworld.generated.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * Greeter service implement
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/13 11:29
 */
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {


    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
//        super.sayHello(request, responseObserver);
        System.out.println("request param is : " +  request.getName());


         // gRPC 生成的服务器端桩始终是异步实现。因此，该方法没有返回值。
         // 相反，您必须使用 StreamObserver 回调接口将数据发回。
         // 由于是完全异步的，因此您还需要调用 onCompleted 来指示服务器已完成响应的发送
        HelloReply response = HelloReply.newBuilder()
                .setMessage("thank you!").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
