/**
 * 
 */
package com.beidouapp.et.util;

/**
 * 校验码工具
 * 
 * @author ztao
 * 
 */
public class CheckCodeUtil {

	/**
	 * 生成异或校验码
	 * 
	 * @param input
	 * @return
	 */
	public static byte getBccCode(byte[] input) {
		if (input == null) {
			return 0;
		}
		byte xor = input[0];
		for (int i = 1; i < input.length; i++) {
			xor ^= input[i];
		}
		return xor;
	}

}
