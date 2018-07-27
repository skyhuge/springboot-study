package com.local.study.netty.diy.codec;

import com.local.study.netty.diy.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private static final Logger logger = LoggerFactory.getLogger(NettyMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> list) throws Exception {
        logger.info("start to encode");
        if(msg == null || msg.getHeader() == null){
            throw new NullPointerException("msg is null");
        }
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        //todo simply JDK serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(bos);
        stream.writeObject(msg);
        stream.close();
        byte[] bytes = bos.toByteArray();
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        list.add(buffer);
    }

}
