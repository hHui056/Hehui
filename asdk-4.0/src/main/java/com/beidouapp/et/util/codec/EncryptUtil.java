package com.beidouapp.et.util.codec;

import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.http.HttpRequest;
import com.beidouapp.et.util.param.CheckingUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加解密工具类.
 *
 * @author mhuang.
 */
public class EncryptUtil {

    /**
     * 生成密码.
     *
     * @param appKey APPKey.
     * @param userId userId.
     * @return 返回加密后的字符串.
     */
    public static String generatedPasswords(String appKey, String userId) {
        String temp = appKey.concat(userId);
        return EncryptUtil.MD5(temp);
    }

    /**
     * 文件模块加密串算法.
     *
     * @param appKey    APPKey
     * @param userId    userId
     * @param randomLen 随机数长度.
     * @return 返回加密后的字符串.
     */
    public static String encryptFileString(String appKey, String userId, int randomLen) {
        StringBuilder sb = new StringBuilder(appKey.length() + 34 + randomLen);
        sb.append(appKey).append(getRandomString(randomLen)).append(userId);
        return EncryptUtil.MD5(sb.toString());
    }

    public static String getRandomString(int len) {
        String randomString = Long.toString(System.currentTimeMillis());
        int length = randomString.length();
        CheckingUtil.checkArgument(length > len, "指定长度大于预设长度!");
        return randomString.substring(length - len);
    }


    /**
     * 32byte md5密钥
     */
    public static String MD5(String sourceStr) {
        StringBuilder result = new StringBuilder(sourceStr.length() + 64);
        try {
            MessageDigest md = MessageDigest.getInstance(EtConstants.MD5);
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result.append(buf.toString());
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
        }
        return result.toString();
    }

    /**
     * sort by ascii
     */
    public static String sort(String str) {
        char[] ch = new char[str.length()];
        char temp;
        for (int i = 0; i < str.length(); i++)
            ch[i] = str.charAt(i);
        for (int j = 0; j < str.length(); j++) {
            for (int i = 0; i < 2; i++)
                if (ch[i] > ch[i + 1]) {
                    temp = ch[i];
                    if (ch[i] < ch[i + 1]) {
                        ch[i + 1] = temp;
                        ch[i] = ch[i + 1];
                    }
                }
        }
        return String.valueOf(ch);
    }

    /**
     * 将二进制数据编码为BASE64字符串
     *
     * @param binaryData 二进制数据.
     * @return 经过BASE64编码的字符串表示.
     */
    public static String encode(byte[] binaryData) {
        return HttpRequest.Base64.encodeBytes(binaryData);
    }

    /**
     * 整形转无符号byte.
     *
     * @param a 整形值.
     * @return 4位short[].
     */
    public static short[] intToByteArray(int a) {
        return new short[]{(short) ((a >> 24) & 0xFF), (short) ((a >> 16) & 0xFF), (short) ((a >> 8) & 0xFF), (short) (a & 0xFF)};
    }

    /**
     * 有符号的2位长度字节数组转无符号的字节表示.
     *
     * @param b 2位长度的有符号字节数组.
     * @return
     */
    public static short byte2Short(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);
        s0 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

}