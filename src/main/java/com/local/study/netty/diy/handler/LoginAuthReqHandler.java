package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LoginAuthReqHandler extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        NettyMessage msg = (NettyMessage) o;
        if (msg != null && msg.getHeader().getType() == MessageType.LOGIN_RESP){
            System.out.println("login response");
            byte body = (byte) msg.getBody();
            if (body != 0){
                System.out.println("handshake failed.");
                ctx.close();
            }else {
                System.out.println("handshake success.");
                ctx.fireChannelRead(o);
            }
        }else {
            ctx.fireChannelRead(o);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginMsg());
    }

    private NettyMessage buildLoginMsg(){
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ);
        msg.setHeader(header);
        return msg;
    }
}
