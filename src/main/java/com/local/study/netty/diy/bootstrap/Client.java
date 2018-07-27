package com.local.study.netty.diy.bootstrap;

import com.local.study.netty.diy.codec.NettyMessageDecoder;
import com.local.study.netty.diy.codec.NettyMessageEncoder;
import com.local.study.netty.diy.handler.HeartBeatReqHandler;
import com.local.study.netty.diy.handler.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.local.study.netty.diy.base.Constant.LOCAL_IP;
import static com.local.study.netty.diy.base.Constant.PORT;

public class Client {

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private void start(String ip,int port){
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
//                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //out
                                    .addLast(new NettyMessageEncoder())
                                    //duplex
                                    .addLast(new ReadTimeoutHandler(30))
                                    //in
                                    .addLast(new NettyMessageDecoder(1024*1024,0,4))
                                    //in
                                    .addLast(new LoginAuthReqHandler())
                                    //in
                                    .addLast(new HeartBeatReqHandler());

                        }
                    });
            Channel channel = b.connect(new InetSocketAddress(ip,port)).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true){

                channel.writeAndFlush(in.readLine() + "\r\n").addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()){
                            System.out.println("cause: " + future.cause());
                        }
                    }
                });
            }
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
