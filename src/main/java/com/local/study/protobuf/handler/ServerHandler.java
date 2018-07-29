package com.local.study.protobuf.handler;

import com.local.study.protobuf.Constant;
import com.local.study.protobuf.MsgBuilder;
import com.local.study.protobuf.ProtobufMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<ProtobufMessage.NettyMsg>{


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.NettyMsg msg) throws Exception {
        int type = msg.getHeader().getType();
        ByteBuf byteBuf;
        if (type == Constant.LOGIN){
            System.out.println("this is a login request from client: ["+ ctx.channel().remoteAddress().toString() +"]");
            byteBuf = MsgBuilder.buildMsg(Constant.LOGIN, "you are welcome");
        }else {
            System.out.println("received data from client: ["+ ctx.channel().remoteAddress().toString() +"]" + ": #" + msg.getBody() + "#");
            byteBuf = MsgBuilder.buildMsg(Constant.CHAT,"did you say "+ msg.getBody());
        }

        ctx.writeAndFlush(byteBuf);
    }
}
