package com.wjr.auto_build.utils;

import java.security.MessageDigest;

/**
 * Created by 王金瑞
 * 2018/12/9
 * 19:03
 * com.wjr.auto_build.utils
 */
public class MD5Utils {
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

    public static String encrypt16(String encryptStr) {
        return encrypt32(encryptStr).substring(8, 24);
    }
}
