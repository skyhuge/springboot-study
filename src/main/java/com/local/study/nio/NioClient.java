package com.local.study.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT);
        sc.connect(new InetSocketAddress(8088));
        ByteBuffer read = ByteBuffer.allocate(512);
        ByteBuffer write = ByteBuffer.allocate(512);
        SocketChannel channel;

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isConnectable()) {
                    channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()) {
                        System.out.println("client connected.");
                        write.clear();
                        write.put("hello server".getBytes());
                        write.flip();
                        channel.finishConnect();
                        System.out.println("client send : hello server.");
                        channel.write(write);
                    }
                    channel.register(selector, SelectionKey.OP_READ);
                }
                else if (key.isReadable()) {
                    channel = (SocketChannel) key.channel();
                    read.clear();
                    int i = channel.read(read);
                    if (i > 0) {
                        System.out.println("client echo: " + new String(read.array(), 0, i));
                    }
                    channel.register(selector, SelectionKey.OP_WRITE);
                }
                else if (key.isWritable()) {
                    channel = (SocketChannel) key.channel();
                    write.clear();
                    write.put(new Date().toString().getBytes());
                    write.flip();
                    channel.write(write);
                    System.out.println("client send: " + new Date().toString());
                    channel.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }
}
