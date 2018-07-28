package com.local.study.protobuf.bootstrap;

import com.local.study.netty.diy.codec.NettyMessageDecoder;
import com.local.study.netty.diy.codec.NettyMessageEncoder;
import com.local.study.netty.diy.handler.HeartBeatReqHandler;
import com.local.study.netty.diy.handler.LoginAuthReqHandler;
import com.local.study.netty.diy.message.Header;
import com.local.study.netty.diy.message.MessageType;
import com.local.study.netty.diy.message.NettyMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import static com.local.study.netty.diy.base.Constant.LOCAL_IP;
import static com.local.study.netty.diy.base.Constant.PORT;

public class Client {

    private void start(String ip,int port){
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline();

                        }
                    });
            Channel channel = b.connect(new InetSocketAddress(ip,port)).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true){


                if (channel.isWritable()){

                    channel.writeAndFlush("todo").addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()){
                                System.out.println("cause: " + future.cause());
                            }

                        }
                    });
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            group.shutdownGracefully();
        }
        /*finally {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    start(LOCAL_IP,PORT);
                }
            });
        }*/
    }

    public static void main(String[] args) {
        new Client().start(LOCAL_IP,PORT);
    }
}
