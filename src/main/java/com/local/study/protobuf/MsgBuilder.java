package com.local.study.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MsgBuilder {

    public static ByteBuf buildMsg(int type, String msg) {

        ProtobufMessage.NettyMsg.Header.Builder builder1 = ProtobufMessage.NettyMsg.Header.newBuilder();
        builder1.setType(type);
        if (type == Constant.LOGIN){
            builder1.setPriority(9);
        }else {
            builder1.setPriority(1);
        }
        ProtobufMessage.NettyMsg.Header header = builder1.build();
        ProtobufMessage.NettyMsg.Body.Builder builder2 = ProtobufMessage.NettyMsg.Body.newBuilder();
        builder2.setBody(msg);
        ProtobufMessage.NettyMsg.Body body = builder2.build();
        ProtobufMessage.NettyMsg.Builder builder = ProtobufMessage.NettyMsg.newBuilder();
        builder.setHeader(header).setBody(body);
        ProtobufMessage.NettyMsg nettyMsg = builder.build();

        byte[] bytes = nettyMsg.toByteArray();
        ByteBuf buffer = Unpooled.buffer(bytes.length + 4);
        buffer.writeByte(builder1.getPriority());
        buffer.writeBytes(bytes);
        return buffer;
    }
}