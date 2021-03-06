package com.local.study.netty.diy.handler;

import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthRespHandler extends SimpleChannelInboundHandler{

    private String[] whiteList = {"127.0.0.1","192.168.131.228"};

    private Map<String,Boolean> map =  new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(LoginAuthRespHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        NettyMessage msg = (NettyMessage) o;
        if (msg != null && msg.getHeader().getType() == MessageType.LOGIN_REQ){

            SocketAddress socketAddress = ctx.channel().remoteAddress();
            String address = socketAddress.toString();
            NettyMessage resp ;
            if (map.containsKey(address)){
                resp = buildRespMsg(MessageType.LOGIN_ER,"you already login.");
            }
            else {
                InetSocketAddress addr = (InetSocketAddress) socketAddress;
                String hostAddress = addr.getAddress().getHostAddress();
                boolean ok = false;
                for (String ip : whiteList) {
                    if (ip.equals(hostAddress)){
                        ok = true;
                        map.put(address,true);
                        break;
                    }
                }
                resp = ok ? buildRespMsg(MessageType.LOGIN_OK,"you are welcome.")
                        : buildRespMsg(MessageType.LOGIN_ER,"you already login.");
            }
            ctx.writeAndFlush(resp);
        }else {

            ctx.fireChannelRead(o);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught, cause:",cause);
        map.remove(ctx.channel().remoteAddress().toString());// clear cache
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildRespMsg(byte type, Object ... obj){
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        if (obj.length > 0){
            Map<String,Object> map = new HashMap<>();
            map.put("msg",obj);
            header.setAttachment(map);
        }
        header.setType(MessageType.LOGIN_RESP);
        msg.setHeader(header);
        msg.setBody(type);
        return msg;
    }

}
