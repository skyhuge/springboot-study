package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAuthReqHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    private static final AttributeKey<Boolean> KEY = AttributeKey.newInstance("active");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        logger.info("channelRead0");
        NettyMessage msg = (NettyMessage) o;
        if (msg != null && msg.getHeader().getType() == MessageType.LOGIN_RESP) {
            logger.info("this is login response");
            byte body = (byte) msg.getBody();
            if (body != 0) {
                logger.info("handshake failed.");
                ctx.close();
            } else {
                logger.info("handshake success.");
                ctx.fireChannelRead(o);
            }
        } else {
            ctx.writeAndFlush(buildLoginMsg());
        }
    }


    /**
     * not work that well ...
     * @return
     */
    /*
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Attribute<Boolean> attr = ctx.channel().attr(KEY);
        Boolean b = attr.get();
        if (b != null) {
            return;
        }
        logger.info("channelActive");
        ctx.writeAndFlush(buildLoginMsg()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    attr.set(Boolean.TRUE);
                    logger.info("writeAndFlush success");
                } else {
                    logger.error("writeAndFlush error");
                    logger.error("cause:", channelFuture.cause());
                }
            }
        });
//        super.channelActive(ctx);
    }*/

    private NettyMessage buildLoginMsg() {
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ);
        msg.setHeader(header);
        return msg;
    }
}
