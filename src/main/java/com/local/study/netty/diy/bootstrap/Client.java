package com.local.study.netty.diy.bootstrap;

import com.local.study.netty.diy.codec.NettyMessageDecoder;
import com.local.study.netty.diy.codec.NettyMessageEncoder;
import com.local.study.netty.diy.handler.HeartBeatReqHandler;
import com.local.study.netty.diy.handler.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.local.study.netty.diy.base.Constant.LOCAL_IP;
import static com.local.study.netty.diy.base.Constant.PORT;

public class Client {

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private void start(String ip,int port){
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024,0,4))
                                    .addLast(new NettyMessageEncoder())
                                    .addLast(new ReadTimeoutHandler(30))
                                    .addLast(new LoginAuthReqHandler())
                                    .addLast(new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture sync = b.connect(new InetSocketAddress(ip,port)).sync();
            sync.channel().closeFuture().sync();
        }
        catch (Exception e){
            e.printStackTrace();
            group.shutdownGracefully();
        }
        finally {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    start(LOCAL_IP,PORT);
                }
            });
        }
    }

    public static void main(String[] args) {
        new Client().start(LOCAL_IP,PORT);
    }
}
