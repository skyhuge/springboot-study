package com.local.study.protobuf.handler;

import com.local.study.protobuf.Constant;
import com.local.study.protobuf.ProtobufMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ProtobufMessage.NettyMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.NettyMsg msg) throws Exception {

        if (msg.getHeader().getType() == Constant.LOGIN){
            System.out.println("so," + msg.getBody());
        }
        else {
            System.out.println("received data from server: " + msg.getBody());
        }
    }
}
