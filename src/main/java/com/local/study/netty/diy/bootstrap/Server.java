package com.local.study.netty.diy.bootstrap;

import com.local.study.netty.diy.codec.NettyMessageDecoder;
import com.local.study.netty.diy.codec.NettyMessageEncoder;
import com.local.study.netty.diy.handler.HeartBeatRespHandler;
import com.local.study.netty.diy.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss,worker).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .option(ChannelOption.SO_REUSEADDR,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024,0,4))
                                    .addLast(new NettyMessageEncoder())
                                    .addLast(new ReadTimeoutHandler(30))
                                    .addLast(new LoginAuthRespHandler())
                                    .addLast(new HeartBeatRespHandler());
                        }
                    });
            ChannelFuture sync = b.bind().sync();
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
