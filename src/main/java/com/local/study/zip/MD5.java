package com.local.study.zip;

import java.security.MessageDigest;

public class MD5 {

    public static String md5(byte[] bytes) throws Exception{
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(bytes);
        byte[] foo = digest.digest();
        return Codec.byteToHex(foo).toUpperCase();
    }
}
