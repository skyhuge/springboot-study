package com.local.study.netty.diy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyMessageDecoder.class);
    
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("start to decode");
        Object decode;
        try{
            decode = super.decode(ctx, in);
        }catch (Exception e){
            return null;
        }

        ByteBuf byteBuf = (ByteBuf)decode ;
        if (null == byteBuf){
            return null;
        }

//        byte[] array = byteBuf.array(); //UnsupportedOperationException: direct buffer
        byte[] array = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,array); // memory copy
        byteBuf.release();
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(array));
        return  stream.readObject();

    }
}
