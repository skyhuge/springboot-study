package com.local.study.zip;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ZipTest {

    public static void main(String[] args) throws Exception {
        d();

    }


    public static void d() throws Exception{
        RandomAccessFile file = new RandomAccessFile(
                new File("/Users/doraemoner/Documents/111"),"rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        for (int i = 0; i < 50000; i++) {
            buffer.put(("hello world " + i).getBytes());
            buffer.put("\r\n".getBytes());
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
        channel.close();
    }
}
