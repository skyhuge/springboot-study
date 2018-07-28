package com.local.study.netty.chat.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //out
        pipeline.addLast("encoder", new StringEncoder());
        //in
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        //in
        pipeline.addLast("decoder", new StringDecoder());
        //in
        pipeline.addLast("handler", new SimpleChatServerHandler());

        System.out.println("SimpleChatClient:" + ch.remoteAddress() + "连接上");
    }
}