package com.local.study.zip;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil {

    public static void compress(String src,String desc) throws Exception{
        /*RandomAccessFile randomAccessFile = new RandomAccessFile(new File(src),"rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
        channel.read(bb);*/
        byte[] bytes =
                IOUtils.toByteArray(new BufferedReader(new FileReader(new File(src))),"utf-8");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(9);
        compressor.setInput(bytes);
        compressor.finish();
        final byte[] buf = new byte[2048];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        compressor.end();
        System.out.println("deflate: "+bos.toByteArray().length);
        IOUtils.write(bos.toByteArray(),new BufferedWriter(new FileWriter(new File(desc))),"utf-8");

    }

    public static void uncompress(String src, String desc) throws Exception{
        /*RandomAccessFile randomAccessFile = new RandomAccessFile(new File(src),"rw");
        FileChannel channel = randomAccessFile.getChannel();
        ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
        channel.read(bb);*/
//        byte[] bytes = bb.array();
        byte[] bytes =

//        IOUtils.read(new BufferedReader(new FileReader(new File(src))),bytes);
        IOUtils.toByteArray(new BufferedReader(new FileReader(new File(src))),"utf-8");
        System.out.println("inflate: "+bytes.length);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        final byte[] buf = new byte[2048];
        while (!inflater.finished()) {
            int count = inflater.inflate(buf);
            bos.write(buf, 0, count);
        }
        inflater.end();
        IOUtils.write(bos.toByteArray(),new BufferedWriter(new FileWriter(new File(desc))),"utf-8");

    }

    public static byte[] deflate(byte[] bytes){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(9);
        compressor.setInput(bytes);
        compressor.finish();
        final byte[] buf = new byte[2048];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        compressor.end();
        return bos.toByteArray();
    }

    public static byte[] inflate(byte[] bytes) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater inflater = new Inflater();
        inflater.setInput(bytes);
        final byte[] buf = new byte[2048];
        while (!inflater.finished()) {
            int count = inflater.inflate(buf);
            bos.write(buf, 0, count);
        }
        inflater.end();
        return bos.toByteArray();
    }
}
