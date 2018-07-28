package com.local.study.protobuf;


import com.google.protobuf.InvalidProtocolBufferException;

public class Test {

    public static void main(String[] args) {
        ProtobufMessage.NettyMsg.Header.Builder builder1 = ProtobufMessage.NettyMsg.Header.newBuilder();
        builder1.setType(1);
        builder1.setPriority(0);
        ProtobufMessage.NettyMsg.Header header = builder1.build();
        ProtobufMessage.NettyMsg.Body.Builder builder2 = ProtobufMessage.NettyMsg.Body.newBuilder();
        builder2.setBody("hello");
        ProtobufMessage.NettyMsg.Body body = builder2.build();
        ProtobufMessage.NettyMsg.Builder builder = ProtobufMessage.NettyMsg.newBuilder();
        builder.setHeader(header).setBody(body);
        ProtobufMessage.NettyMsg nettyMsg = builder.build();

        System.out.println("msg: " + nettyMsg.toString());

        System.out.println(nettyMsg.toByteString());

        byte[] bytes = nettyMsg.toByteArray();
        try {
            ProtobufMessage.NettyMsg msg = ProtobufMessage.NettyMsg.parseFrom(bytes);
            System.out.println(msg.toString());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
