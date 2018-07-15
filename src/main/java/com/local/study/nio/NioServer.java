package com.local.study.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Set;

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

    private void listen() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey selectionKey : selectionKeys) {
                handleKeys(selectionKey);
            }
        }
    }

    private void handleKeys(SelectionKey key) throws IOException {
        ServerSocketChannel ssc;
        SocketChannel accept;
        if (key.isAcceptable()) {
            ssc = (ServerSocketChannel) key.channel();
            accept = ssc.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            accept = (SocketChannel) key.channel();
            read.clear();
            int reader = accept.read(this.read);
            if (reader > 0) {
                String s = new String(read.array(), 0, reader);
                System.out.println("server received : " + s);
            }
            accept.register(selector, SelectionKey.OP_WRITE);
        } else if (key.isWritable()) {
            accept = (SocketChannel) key.channel();
            write.clear();
            write.put(new Date().toString().getBytes());
            write.flip();
            accept.write(write);
            System.out.println("server echo: " + new Date().toString());
            accept.register(selector, SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer(8088);
        nioServer.listen();
    }
}
