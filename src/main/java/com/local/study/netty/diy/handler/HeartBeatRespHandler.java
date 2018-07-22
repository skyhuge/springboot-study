package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HeartBeatRespHandler extends SimpleChannelInboundHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        NettyMessage msg = (NettyMessage) o;
        if (null!=msg && msg.getHeader().getType() == MessageType.HEART_REQ){
            ctx.writeAndFlush(buildRespMsg());
        }else {
            ctx.fireChannelRead(o);
        }
    }

    private NettyMessage buildRespMsg(){
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEART_RESP);
        msg.setHeader(header);
        return msg;
    }
}
