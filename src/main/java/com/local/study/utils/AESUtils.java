package com.local.study.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;


public class AESUtils {


    /**
     * 随机生成密钥的数据源
     */
    private static final String KEY_SOURCE = "qwertyuiopasdfghjklzxcvbnm1234567890";


    static final int AES_128 = 128;
    static final int AES_192 = 192;
    static final int AES_256 = 256;

    /**
     * 生成指定类型的AESkey的长度
     *
     * @param type AES类型
     * @return key
     */
    public static String createAESKey(int type) {
        int length = type / 8;
        StringBuffer keySB = new StringBuffer();
        SecureRandom random = new SecureRandom();
        int sourceL = KEY_SOURCE.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(sourceL);
            keySB.append(KEY_SOURCE.charAt(index));
        }
        return keySB.toString();
    }

    /**
     * AES加密，返回秘文
     *
     * @param plaintext 明文
     * @param key       加密key
     * @param type      加密类型
     * @return 秘文
     */
    public static String encryptAES(String plaintext, String key, int type) {
        byte[] result = encrypt(plaintext, key, type);
        return parseByte2HexStr(result);
    }

    /**
     * AES解密
     *
     * @param ciphertext 秘文
     * @param key        加密key
     * @param type       加密类型
     * @return 明文
     */
    public static String decryptAES(String ciphertext, String key, int type) {
        byte[] result = parseHexStr2Byte(ciphertext);
        byte[] plainByte = decrypt(result, key, type);
        try {
            return new String(plainByte, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt(String content, String password, int aesLength) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(aesLength, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(byteContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content   待解密内容
     * @param password  解密密钥
     * @param aesLength AES加密类型，128、192、256
     * @return
     */
    public static byte[] decrypt(byte[] content, String password, int aesLength) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(aesLength, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    // 测试
    public static void main(String[] args) {
//        String key = createAESKey(AES_192);
        String key = "asdf123654";
        System.out.println("密钥：" + key);
        String plaintext = "AES Test!";
        String ciphertext = encryptAES(plaintext, key, AES_128);
        String ciphertext1 = encryptAES(plaintext, key, AES_192);
        String ciphertext2 = encryptAES(plaintext, key, AES_256);
        System.out.println("秘文：" + ciphertext);
        System.out.println("秘文：" + ciphertext1);
        System.out.println("秘文：" + ciphertext2);
        plaintext = decryptAES(ciphertext1, key, AES_192);
        System.out.println("明文：" + plaintext);
    }

}


