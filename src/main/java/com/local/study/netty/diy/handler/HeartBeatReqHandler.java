package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends SimpleChannelInboundHandler{

    private volatile ScheduledFuture<?> heart;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        NettyMessage msg = (NettyMessage) o;
        if (null != msg && msg.getHeader().getType() == MessageType.HEART_RESP){
            System.out.println("received heart beat resp: " + msg);
        }
        else if (null != msg && msg.getHeader().getType() == MessageType.LOGIN_RESP){
            heart = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx),0,5000, TimeUnit.MILLISECONDS);
        }else {
            ctx.fireChannelRead(o);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != heart){
            heart.cancel(true);
            heart = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    private class HeartBeatTask implements Runnable{

        private ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage msg = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEART_REQ);
            msg.setHeader(header);
            ctx.writeAndFlush(msg);
        }
    }
}
