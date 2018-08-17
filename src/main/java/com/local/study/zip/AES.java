package com.local.study.zip;

import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    public static byte[] encrypt(byte[] content, String password) {
        try {
            Cipher cipher = getCipher(password,Cipher.ENCRYPT_MODE);
            return cipher.doFinal(content);// 加密
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
            Cipher cipher = getCipher(password,Cipher.DECRYPT_MODE);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Cipher getCipher(String password, int mode) throws Exception{
        KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
        byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
        cipher.init(mode, key);// 初始化为解密模式的密码器
        return cipher;
    }

    public static void main(String[] args) throws Exception{
        String key = "123456";
        byte[] bytes = IOUtils.toByteArray(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\111")), "utf-8");
        System.out.println("bytes: " + bytes.length);
        System.out.println(MD5.md5(bytes));

        byte[] deflate = ZipUtil.deflate(bytes);
        System.out.println("deflate: " + deflate.length);
//        IOUtils.write(deflate,new FileOutputStream(new File("/Users/doraemoner/Documents/a")));

        byte[] encrypt = encrypt(deflate, key);//加密后大小没变？
        System.out.println("encrypt: " + encrypt.length);

//        String hex = Codec.byteToHex(encrypt);//转十六进制后变成2倍？考虑Base64
//        System.out.println("hex: " + hex.getBytes().length);
        IOUtils.write(encrypt,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\aaa")));
//        IOUtils.write(hex,new FileOutputStream(new File("/Users/doraemoner/Documents/bb")),"utf-8");


//        byte[] hexToByte = Codec.hexToByte(hex);
//        System.out.println("hexToByte: " + hexToByte.length);

        byte[] decrypt = decrypt(encrypt, key);
//        byte[] decrypt = decrypt(hexToByte, key);
        System.out.println("decrypt: " + decrypt.length);
//        IOUtils.write(decrypt,new FileOutputStream(new File("/Users/doraemoner/Documents/c")));

        byte[] inflate = ZipUtil.inflate(decrypt);
        System.out.println("inflate: " + inflate.length);
        IOUtils.write(inflate,new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\bbb")));
        System.out.println(MD5.md5(inflate));

    }
}
