package com.local.study.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class WebSocketNettyServer {

    private static final int port = 8080;

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new WebSocketServerHandler());

                        }
                    });
            Channel channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class WebSocketServerHandler extends SimpleChannelInboundHandler{

        private WebSocketServerHandshaker handshaker;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

            if (msg instanceof FullHttpRequest){

                handleHttpRequest(ctx,msg);

            }else if (msg instanceof WebSocketFrame){

                handleWebSocket(ctx,msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        private void handleHttpRequest(ChannelHandlerContext ctx, Object msg){
            FullHttpRequest request = (FullHttpRequest) msg;
            if (request.decoderResult().isFailure()
                    || !"websocket".equals(request.headers().get("Upgrade"))){
                sendHttpResponse(ctx,request,
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
                return;
            }

            WebSocketServerHandshakerFactory factory =
                    new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket",null,false);
            handshaker = factory.newHandshaker(request);
            if (null == handshaker){
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }else {
                handshaker.handshake(ctx.channel(),request);
            }
        }

        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {

            if (response.status().code() != 200){
                ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
                response.content().writeBytes(byteBuf);
                byteBuf.release();
                HttpUtil.setContentLength(response,response.content().readableBytes());
            }

            ChannelFuture future = ctx.channel().writeAndFlush(response);
            if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

        private void handleWebSocket(ChannelHandlerContext ctx, Object msg){

            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof CloseWebSocketFrame){
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            if (frame instanceof PingWebSocketFrame){
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            if (!(frame instanceof TextWebSocketFrame)){
//                ctx.channel().write()
                // TODO: 2018/7/21
                throw new UnsupportedOperationException();
            }

            String req = ((TextWebSocketFrame)frame).text();
            System.out.println("server received: "+ req);

            for (int i = 0; i < 10; i++) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame(" Now : "+ i));
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
