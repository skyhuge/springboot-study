package com.local.study.netty.diy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
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

        byte[] array = byteBuf.array();
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(array));
        return  stream.readObject();

    }
}
