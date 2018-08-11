package com.local.study.zip;

public class Codec {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    //用2个hex来表示一个byte，一个hex表示byte的高四位，另外一个表示byte的低四位
    //所以转换后大小会翻倍
    public static String byteToHex(byte[] bytes) {

        if (null == bytes || bytes.length == 0) return null;
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {

            // 当byte值为负数时，获得其补码 -1 >> 255
            int v = bytes[j] & 0xFF; // 0xFF = 255

            hexChars[j * 2] = hexArray[v >>> 4];//  v/16，表示高四位

            hexChars[j * 2 + 1] = hexArray[v & 0x0F];//得到余数，表示低四位

        }
        return new String(hexChars);
    }

    public static byte[] hexToByte(String hex) {

        if (null == hex || hex.equals("")) return null;
        byte[] bytes = new byte[hex.length() >> 1];
        for (int i = 0; i < hex.length(); i += 2) {
            //高位乘以16 + 低位 还原成一个byte
            //将int强转成byte的时候会截断后8位 255 >> -1
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    | Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }


    public static void main(String[] args) {
        byte[] bytes = "中国".getBytes();
        for (int i = 0; i < bytes.length; i++) {

            System.out.print(bytes[i]);
        }
        System.out.println();
        System.out.println(byteToHex(bytes));

        String hex = "E4B8ADE59BBD";
        System.out.println(new String(hexToByte(hex)));

        byte c = -28;
        int j = c & 0xFF;
        byte t = (byte) (15*16 | 15);
        System.out.println(j);
        System.out.println(t);
    }
}
