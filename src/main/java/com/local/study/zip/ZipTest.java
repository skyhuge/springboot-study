package com.local.study.zip;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ZipTest {

    public static void main(String[] args) throws Exception {
//        d();
        /*ZipUtil.compress("C:\\Users\\Administrator\\Desktop\\111",
                "C:\\Users\\Administrator\\Desktop\\222");
        ZipUtil.uncompress("C:\\Users\\Administrator\\Desktop\\222",
                "C:\\Users\\Administrator\\Desktop\\333");*/

        byte[] bytes = IOUtils.toByteArray(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\111")), "utf-8");
        System.out.println("bytes: " + bytes.length);

        byte[] deflate = ZipUtil.deflate(bytes);
        System.out.println("deflate: " + deflate.length);
        IOUtils.write(deflate,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\222")));

//        byte[] inflate = ZipUtil.inflate(deflate);

        byte[] inflate = ZipUtil.inflate(deflate);
        System.out.println("inflate: " + inflate.length);

        IOUtils.write(inflate,new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\333")));
//        IOUtils.write(inflate,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\444")));
        Thread.sleep(2000L);
    }


    public static void d() throws Exception{
        RandomAccessFile file = new RandomAccessFile(
                new File("C:\\Users\\Administrator\\Desktop\\111"),"rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        for (int i = 0; i < 500000; i++) {
            buffer.put(("hello world " + i).getBytes());
            buffer.put("\r\n".getBytes());
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
        channel.close();
    }
}
