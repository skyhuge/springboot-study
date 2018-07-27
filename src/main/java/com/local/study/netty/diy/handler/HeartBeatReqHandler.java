package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends SimpleChannelInboundHandler{

    private volatile ScheduledFuture<?> heart;

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatReqHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        logger.info("channelRead start");
        NettyMessage msg = (NettyMessage) o;
        if (null != msg && msg.getHeader().getType() == MessageType.HEART_RESP){
            logger.info("received heart beat resp: " + msg);
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
            logger.info("HeartBeatTask run.");
            NettyMessage msg = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEART_REQ);
            msg.setHeader(header);
            ctx.writeAndFlush(msg);
        }
    }
}
