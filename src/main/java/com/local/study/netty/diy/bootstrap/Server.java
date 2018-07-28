package com.local.study.netty.diy.bootstrap;

import com.local.study.netty.diy.base.Constant;
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

public class Server {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(2);
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss,worker).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //out
                                    .addLast(new NettyMessageEncoder())
                                    //duplex
//                                    .addLast(new ReadTimeoutHandler(30))
                                    //in
                                    .addLast(new NettyMessageDecoder(1024,0,4))
                                    //in
                                    .addLast(new LoginAuthRespHandler())
                                    //in
                                    .addLast(new HeartBeatRespHandler());

                        }
                    });
            ChannelFuture sync = b.bind(Constant.PORT).sync();
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
