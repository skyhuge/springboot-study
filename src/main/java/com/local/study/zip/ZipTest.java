package com.local.study.zip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipTest {

    public static void main(String[] args) throws Exception {
//        d();
        //0FC0803A18872AFD7AA01E4E21A8C347
//        a();
//        b();
        p();
        e();
        f();
        i();
    }

    /**
     * 无填充加密
     */
    public static void e() throws Exception{
        int bufferSize = 1024 * 1024;
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer(bufferSize);
        Deflater deflater = ZipUtil.getDeflater(9);
        Cipher cipher = AES.getCipher("123456", Cipher.ENCRYPT_MODE);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\111")),bufferSize);
        OutputStream writer = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\222"), true);
        byte[] bytes = new byte[bufferSize];
        int max = 0;
        int len;
        List<Integer> list = new ArrayList<>();
        while ((reader.read(bytes)) != -1){//每次读1M
            byte[] deflate = ZipUtil.deflate(bytes, deflater);//压缩后长度不确定
            max ++;
            len = deflate.length;
            byteBuf.writeBytes(deflate,0,len);//先保存已知大小
            list.add(len);
            System.out.println("count: " + max + ", length: " + len);

        }
        System.out.println("before append: " + byteBuf.readableBytes());
        Integer[] objects = new Integer[list.size()];
        list.toArray(objects);
        for (Integer object : objects) {
            byteBuf.writeInt(object); //每次压缩后的长度
        }
        byteBuf.writeInt(list.size()); //压缩的次数
        System.out.println("after append: " + byteBuf.readableBytes());

        byte[] b= new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b,0,byteBuf.readableBytes());
        System.out.println("待加密数组长度: "+b.length);

        byte[] aFinal = cipher.doFinal(b);
        writer.write(aFinal,0,aFinal.length);
    }

    /**
     * 无填充解密
     */
    public static void f() throws Exception{
        int bufferSize = 1024 * 1024;
        Cipher cipher = AES.getCipher("123456", Cipher.DECRYPT_MODE);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\222")),bufferSize);
        byte[] byteArray = IOUtils.toByteArray(reader);
        OutputStream writer = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\333"), true);
        Inflater inflater = ZipUtil.getInflater();
        byte[] bytes = new byte[4];

        byte[] doFinal = cipher.doFinal(byteArray);
        int length = doFinal.length;
        System.out.println("解密出来数组长度: "+ length);

        for (int i = 3; i >= 0 ; i--) {
            bytes[4 -1 -i] = doFinal[length-1 - i];
        }
        int len = getInt(bytes,0);//压缩的次数
        System.out.println("压缩的次数: " + len);
        bytes = new byte[len * 4];
        for (int i = len * 4 -1; i >= 0 ; i--) {
            bytes[len * 4- 1 -i] = doFinal[length-1 - i];
        }
        int[] t = new int[len];
        for (int i = 0,j = 0; i < len; i++,j+=3) {
            t[i] = getInt(bytes,j);
        }
        int index = 0;
        int count = 0;
        for (int i : t) {
            count ++;
            System.out.println("count: " + count + ", length: " + i);
            System.arraycopy(doFinal,index,bytes,0,i);
            ZipUtil.inflate(bytes, inflater,writer);
            index += i;
        }
    }

    private static int getInt(byte[] memory, int index){
       return  (memory[index] & 255) << 24 | (memory[index + 1] & 255) << 16
               | (memory[index + 2] & 255) << 8 | memory[index + 3] & 255;
    }

    /**
     * 比对md5
     */
    public static void i() throws Exception{
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\333")));
        System.out.println("333 :" + bytes.length);
        String s = MD5.md5(bytes);

        byte[] bytes1 = IOUtils.toByteArray(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\111")));
        System.out.println("111 :" + bytes1.length);
        String s1 = MD5.md5(bytes1);

        System.out.println(s.equals(s1));
    }

    public static void p(){
        File f1 = new File("C:\\Users\\Administrator\\Desktop\\222");
        File f2 = new File("C:\\Users\\Administrator\\Desktop\\333");
        if (f1.exists()) f1.delete();
        if (f2.exists()) f2.delete();
    }

    /**
     * 解密后按固定长度解压
     */
    public static void b() throws Exception{
        int bufferSize = 1024 * 1024;
        int half = bufferSize >> 1;
        Cipher cipher = AES.getCipher("123456", Cipher.DECRYPT_MODE);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\222")),bufferSize);
        byte[] byteArray = IOUtils.toByteArray(reader);
        OutputStream writer = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\333"), true);
        Inflater inflater = ZipUtil.getInflater();
        byte[] bytes = new byte[half];

        byte[] doFinal = cipher.doFinal(byteArray);//解密出来是n个0.5M字节数组
        System.out.println("解密出来数组长度: "+doFinal.length);

        int loop = doFinal.length / bufferSize * 2;
        System.out.println("loop: " + loop);
        for (int i = 0; i < loop; i++) {
            System.arraycopy(doFinal,i * half,bytes,0,half);
            byte[] inflate = ZipUtil.inflate(bytes, inflater);
            writer.write(inflate,0,inflate.length);
        }
    }

    /**
     * 按固定长度填充nul后加密
     */
    public static void a() throws Exception{
        int bufferSize = 1024 * 1024;
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        Deflater deflater = ZipUtil.getDeflater(9);
        Cipher cipher = AES.getCipher("123456", Cipher.ENCRYPT_MODE);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\111")),bufferSize);
        OutputStream writer = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\222"), true);
        byte[] bytes = new byte[bufferSize];
        int read = 0;
        while ((reader.read(bytes)) != -1){//每次读1M
            read ++;
            byte[] deflate = ZipUtil.deflate(bytes, deflater);//压缩后长度不确定
            byteBuf.writeBytes(deflate,0,deflate.length);//先保存已知大小
            int size = (bufferSize >> 1) - deflate.length;//统一调整为0.5M
            if (size > 0){
                byteBuf.writeBytes(new byte[size]);
            }
        }
        System.out.println(read);
        byte[] b= new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b,0,byteBuf.readableBytes());
        System.out.println("待加密数组长度: "+b.length);
        byte[] aFinal = cipher.doFinal(b);//加密的时候,是对n个0.5M字节数组加密
        writer.write(aFinal,0,aFinal.length);
    }

    /**
     * 生成测试文件
     */
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
