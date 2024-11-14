package com.runner.serializer.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.runner.serializer.protobuf.generated.MyProtocol;

/**
 * protobuf serialization demo
 *
 * @author Runner
 * @version 1.0
 * @since 2024/11/14 13:14
 */
public class ProtobufDemo {
    public static void main(String[] args) {
        MyProtocol.User user = MyProtocol.User.newBuilder().setName("张三").setAge(18).build();

        byte[] byteArray = user.toByteArray();

        System.out.println("data length:" + byteArray.length);

        try {
            MyProtocol.User parsed = MyProtocol.User.parseFrom(byteArray);
            System.out.println(parsed.getName());
            System.out.println(parsed.getAge());
            System.out.println(parsed.getEmail());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
