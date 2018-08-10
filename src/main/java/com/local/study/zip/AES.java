package com.local.study.zip;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;

public class AES {

    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            // 128位的key生产者
            //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行

            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥

            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回null。

            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥

            Cipher cipher = Cipher.getInstance("AES");// 创建密码器

            byte[] byteContent = content.getBytes("utf-8");

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(content);
            return result; // 明文

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception{
        String key = "1234567890123456";
        byte[] bytes = IOUtils.toByteArray(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\111")), "utf-8");
        System.out.println("bytes: " + bytes.length);

        byte[] encrypt = encrypt(new String(bytes), key);
        System.out.println("encrypt: " + encrypt.length);
        IOUtils.write(encrypt,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\aaa")));

        byte[] deflate = ZipUtil.deflate(encrypt);
        System.out.println("deflate: " + deflate.length);
        IOUtils.write(deflate,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\bbb")));

        byte[] inflate = ZipUtil.inflate(deflate);
        System.out.println("inflate: " + inflate.length);
        IOUtils.write(inflate,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\ccc")));

        byte[] decrypt = decrypt(inflate, key);
        System.out.println("decrypt: " + decrypt.length);
        IOUtils.write(decrypt,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\ddd")));
    }
}
