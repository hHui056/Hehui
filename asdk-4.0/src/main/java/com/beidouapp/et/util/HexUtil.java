/**
 *
 */
package com.beidouapp.et.util;

import java.io.UnsupportedEncodingException;

/**
 * 十六进制实体类
 * 
 * @author shilintang
 * @date 2012-10-26
 * @version 1.0
 */
public class HexUtil {

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            十六进制字符串 String
	 * @return byte[]
	 */
	public static byte[] hexString2Bytes(String src) {
		if (src == null) {
			return null;
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 只能把一个字节长度的16进制字符串转换成整数
	 * 
	 * @param src
	 * @return
	 */
	public static int hexString2int(String src) {
		byte[] b = hexString2Bytes(src);
		int c = ((b[1] << 8)) | b[0];
		return c;
	}

	/**
	 * 把int转换成指定长度的十六进制字符串
	 * 
	 * @param src
	 * @param len
	 * @return
	 */
	public static String int2HexString(int src, int len) {
		String hexStr = Integer.toHexString(src).toUpperCase();
		while (hexStr.length() < len) {
			hexStr = "0" + hexStr;
		}
		return hexStr;
	}

	/**
	 * 浮点数转换成hex字符串（长度为8）
	 */
	public static String float2HexString(float f) {
		int i = Float.floatToIntBits(f);
		String s = Integer.toHexString(i);
		return s;
	}

	/**
	 * hex字符串(长度为8)转换成浮点数
	 */
	public static float hexString2Float(String s) {
		if (s.length() != 8) {
			return Float.NaN;
		}
		try {
			int ibits = Integer.parseInt(s, 16);
			return Float.intBitsToFloat(ibits);
		} catch (Exception e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static String printHexString(byte[] b) {
		if (b == null) {
			return null;
		}
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			returnValue.append(hex.toUpperCase());
		}
		return returnValue.toString();
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台，如果长度不足指定的长度，在后面补'0'。
	 * 
	 * @param b
	 * @param len
	 *            指定的长度
	 * @return
	 */
	public static String printHexStringWith0Post(byte[] b, int len) {
		String s = printHexString(b);
		StringBuilder sb = new StringBuilder();
		if (s.length() < len) {
			int add0Count = len - s.length();
			for (int i = 0; i < add0Count; i++) {
				sb.append("0");
			}
			s = s + sb.toString();
		}
		return s;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台，如果长度不足指定的长度，在前面补'0'。
	 * 
	 * @param b
	 * @param len
	 * @return
	 */
	public static String printHexStringWith0Pre(byte[] b, int len) {
		String s = printHexString(b);
		StringBuilder sb = new StringBuilder();
		if (s.length() < len) {
			int add0Count = len - s.length();
			for (int i = 0; i < add0Count; i++) {
				sb.append("0");
			}
			s = sb.toString() + s;
		}
		return s;
	}

	/**
	 * 计算校验和
	 * 
	 * @param src
	 * @return
	 */
	public static String checkSum(String src) {
		if (src == null) {
			return null;
		}
		int length = src.length();
		String sumString = null;
		for (int i = 0; i < length; i = i + 2) {
			sumString = sumHex(sumString, src.substring(i, i + 2));
		}

		return sumString.substring(sumString.length() - 2, sumString.length()).toUpperCase();
	}

	private static String sumHex(String src1, String src2) {
		if (src1 == null) {
			return src2;
		}

		long x = Long.parseLong(src1, 16);
		long y = Long.parseLong(src2, 16);

		return Long.toHexString(x + y);
	}

	/**
	 * 16进制数自增1
	 * 
	 * @param src
	 * @return
	 */
	public static String increase(String src) {
		if (Long.parseLong(src, 16) >= 100) {
			return src;
		}
		long x = Long.parseLong(src, 16);
		String tmp = Long.toHexString(x + 1).toUpperCase();
		if (tmp.length() == 1) {
			return "0" + tmp;
		}
		return tmp.substring(tmp.length() - 2, tmp.length()).toUpperCase();
	}

	/**
	 * 16进制数自减1
	 * 
	 * @param src
	 * @return
	 */
	public static String reduce(String src) {
		if (Long.parseLong(src, 16) <= 0) {
			return src;
		}
		long x = Long.parseLong(src, 16);
		String tmp = Long.toHexString(x - 1).toUpperCase();
		if (tmp.length() == 1) {
			return "0" + tmp;
		}
		return tmp.substring(tmp.length() - 2, tmp.length()).toUpperCase();
	}

	/**
	 * 把指定编码方式的字符串的十六进制形式转换成可读形式。 例如："303132" -> "012"
	 * 
	 * @param hexStr
	 * @param encode
	 *            编码方式，"ascii"，"utf-8"...
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String hexStr2Str(String hexStr, String encode) throws UnsupportedEncodingException {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes, encode);
	}

	public static void main(String[] args) {
		try {
			System.out.println(HexUtil.hexStr2Str("303132", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
