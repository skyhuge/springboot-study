package com.local.study.netty.diy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (null == byteBuf){
            return null;
        }

        byte[] array = byteBuf.array();
        ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(array));
        return  stream.readObject();

    }
}
