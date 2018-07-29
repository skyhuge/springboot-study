package com.local.study.protobuf.bootstrap;

import com.local.study.protobuf.Constant;
import com.local.study.protobuf.MsgBuilder;
import com.local.study.protobuf.ProtobufMessage;
import com.local.study.protobuf.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import static com.local.study.netty.diy.base.Constant.LOCAL_IP;
import static com.local.study.netty.diy.base.Constant.PORT;

public class Client {

    private void start(String ip, int port) {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
//                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(1024,0,4))
                                    .addLast(new ProtobufDecoder(ProtobufMessage.NettyMsg.getDefaultInstance()))
//                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new ClientHandler())
                            ;

                        }
                    });
            Channel channel = b.connect(new InetSocketAddress(ip, port)).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String s = in.readLine();
                ByteBuf msg;
                if (s.equals("hello")) {
                    msg = MsgBuilder.buildMsg(Constant.LOGIN, s);
                } else {
                    msg = MsgBuilder.buildMsg(Constant.CHAT, s);
                }

                if (channel.isWritable()) {

                    channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                System.out.println("cause: " + future.cause());
                                future.addListener(CLOSE);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    private void buildReq(int type) {

        if (Constant.LOGIN == type) {

        }
    }

    public static void main(String[] args) {
        new Client().start(LOCAL_IP, PORT);
    }
}
