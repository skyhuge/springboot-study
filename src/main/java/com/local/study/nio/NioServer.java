package com.local.study.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioServer {

    private Selector selector;

    private ByteBuffer read = ByteBuffer.allocate(512);

    private ByteBuffer write = ByteBuffer.allocate(512);

    public NioServer(int port) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ServerSocket serverSocket = ssc.socket();
            serverSocket.bind(new InetSocketAddress(port));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws Exception {
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                handleKeys(iterator.next());
                iterator.remove();
            }
        }
    }

    private void handleKeys(SelectionKey key) throws Exception {
        ServerSocketChannel ssc;
        SocketChannel accept;
        if (key.isAcceptable()) {
            System.out.println("isAcceptable");
            ssc = (ServerSocketChannel) key.channel();
            accept = ssc.accept();
            if (null != accept){
                accept.configureBlocking(false);
                accept.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }else {
                System.out.println("we did not get a socketChannel, sleep for a while.");
                Thread.sleep(1000L);
            }
        } else if (key.isReadable()) {
            System.out.println("isReadable");
            accept = (SocketChannel) key.channel();

            read.clear();
            int reader = accept.read(this.read);
            if (reader > 0) {
                String s = new String(read.array(), 0, reader);
                System.out.println("server received : " + s);
                write.clear();
                write.put(s.getBytes());
                write.flip();
                accept.write(write);
                System.out.println("server echo: " + s);
            }
//            accept.register(selector, SelectionKey.OP_WRITE,s); //attach message received from client
        }
        /*else if (key.isWritable()) {
            System.out.println("isWritable");
            accept = (SocketChannel) key.channel();
            List attachment = (List) key.attachment();
            if (attachment.size() == 0){
                System.out.println("empty list now.");
            }else {
                write.clear();
                write.put(((String)attachment.get(0)).getBytes());
                attachment.clear();
                write.flip();
                accept.write(write);
                System.out.println("server echo: " + attachment);
            }
        }*/
    }

    public static void main(String[] args) throws Exception {
        NioServer nioServer = new NioServer(8088);
        nioServer.listen();
    }
}
